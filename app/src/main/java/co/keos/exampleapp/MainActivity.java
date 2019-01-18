package co.keos.exampleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import co.chatsdk.core.error.ChatSDKException;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.firebase.generalfunc.PengiAgentAvailableCallback;
import co.chatsdk.firebase.generalfunc.PengiAuthCallback;
import co.chatsdk.firebase.generalfunc.PengiFunctions;
import co.keos.exampleapp.chat.ChatActivity;
import co.keos.exampleapp.chat.UtilsForApp;

public class MainActivity extends AppCompatActivity {

    //Used by PengiChatSDK
    public static final String TAG = "MainActivity";
    Button chatButton;
    Context context;
    Activity activity;
    PengiFunctions pengiChatAuthorization;
    Configuration.Builder config;
    Intent intentChatActivity;
    int mainLayout;
    //END-Used by PengiChatSDK

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sdk init vars
        initMainVars();
    }


    /**
     * Init the main vars  SDK initialize
     */
    private void initMainVars() {
        //We should to initialize these variables to be used by SDK
        chatButton = findViewById(R.id.chatButton);
        context = getApplicationContext();
        activity = MainActivity.this;
        mainLayout = R.id.mainConstraintLayout;
    }

    /**
     * Helps to setup all the articulations of the chatSdk.
     */
    public void chatSdk(){
        setSdkConfig();
        initializeChatSdk();
        initChatFunctions();
        onChatButtonClickResult();
    }

    /**
     * Here most to call to the resumeConnection method and the main setup chatSDK method. This will
     * helps to install the ChatSdk main configuration and helps with the authentication.
     */
    @Override
    protected void onResume(){
        super.onResume();
        PengiFunctions.resumeConnection();
        chatSdk();
    }

    /**
     * Here we should intent to close any opened chat session when the app is finishing.
     */
    @Override
    protected void onDestroy() {
        if(isFinishing()) ChatActivity.closeChat();
        super.onDestroy();
    }

    /**
     * Sets authentication values and some chatSDK configuration settings.
     */
    public void setSdkConfig(){
        //We should instance the Configuration class and start the Builder.
        config = new Configuration.Builder(context);

        //Authentication token provided by Keos or from the Pengi administrator panel.
        config.setAuthToken("$2y$10$bpc10IKwgmxP0uzze5D75u2VwVl/ZDVY5f7Mj/Z0Q0OVGJ2hmYG8O");

        //App id
        config.setOriginApp("co.keos.myapplication");

        //Clients name, this will appears in the agents graphic interface. Optional param
        config.setNameUser("Juan");

        //Clients email, this will appears in the agents graphic interface. Optional param
        config.setUserEmail("juancardenas@keos.co");

        //Clients ubication. Optional param
        config.setLocation("Popayán");

        //Time in seconds to close the chat session when the user stops the chat or kill the app. Optional param
        config.setTimeToKillChat(30); //Default value is 60
    }

    /**
     * Helps to initialize the Chat SDK with the main configuration parameters.
     * Important to make the firebase connection.
     */
    public void initializeChatSdk(){
        try {
            ChatSDK.initialize(config.build(), null, new FirebaseNetworkAdapter());
        }catch (ChatSDKException e) {
            Log.e("Error", "initializeChatSdk ChatSDKException:" + TAG + " " + e);
        }
    }

    /**
     * This method helps to notify developers through some callbacks about main authentication and
     * ChatSdk initialization
     */
    public void initChatFunctions(){
        pengiChatAuthorization = new PengiFunctions(ChatSDK.config().nameUser, ChatSDK.config().userEmail, ChatSDK.config().location, ChatSDK.config().authToken, ChatSDK.config().originApp, context, new PengiAuthCallback() {
            @Override
            public void onAuthenticationSuccess(){
                chatButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAuthenticationFail(){
                chatButton.setVisibility(View.GONE);
            }

            @Override
            public void onCallFailure(){
                chatButton.setVisibility(View.GONE);
            }
        });
    }

    /**
     * This method helps to notify developers through some callbacks about the possible events when the client clicks the chat Button.
     */
    void onChatButtonClickResult(){
        chatButton.setOnClickListener(view -> pengiChatAuthorization.searchForAgents(new PengiAgentAvailableCallback() {

            /**
             * Notifies when the task of search an agent is initiated.
             */
            @Override
            public void onSearchAgent() {
                Toast.makeText(context, "Searching for agents...", Toast.LENGTH_SHORT).show();
                setChatButtonOnClickConfig(false, "Searching for agents");
            }

            /**
             * Notifies when an agent is found available.
             */
            @Override
            public void onAgentAvailable() {
                Toast.makeText(context, "An agent is available", Toast.LENGTH_SHORT).show();
                setChatButtonOnClickConfig(false, "Chat online");
                goToChat();
            }

            /**
             * Notifies when there is no agents available
             */
            @Override
            public void noAgentsAvailable() {
                setChatButtonOnClickConfig(true, "Chat online");
                UtilsForApp.openPopUpWindow(activity,"There is no agents available, please try again...", mainLayout,true);
                Toast.makeText(context, "Our agents are busy", Toast.LENGTH_LONG).show();
            }

            /**
             * Notifies when the api call fails
             */
            @Override
            public void onCallFailure() {
                Toast.makeText(context, "This request fail, please try again...", Toast.LENGTH_LONG).show();
                setChatButtonOnClickConfig(true, "Chat online");
            }

            /**
             * Notifies when is not a workday and shows a default message provided from Pengi administrator panel
             * @param messageSchedule String with the not available on time message.
             */
            @Override
            public void onUnavailableSchedule(String messageSchedule) {
                UtilsForApp.openPopUpWindow(activity, messageSchedule, mainLayout,true);
                chatSdk();
            }

        }));
    }

    /**
     * Setups the chat button status and text message when is called.
     * @param buttonEnabled boolean, if the button will be enable or not
     * @param textForButton String with the text to show.
     */
    public void setChatButtonOnClickConfig(boolean buttonEnabled, String textForButton) {
        chatButton.setEnabled(buttonEnabled);
        chatButton.setText(textForButton);
    }

    /**
     * Makes the intent to go to the chat activity (Where the main chat activity will be)
     */
    public void goToChat(){
        intentChatActivity = new Intent(context, ChatActivity.class);
        intentChatActivity.putExtra("chatFunctions", pengiChatAuthorization);
        startActivity(intentChatActivity);
    }



}
