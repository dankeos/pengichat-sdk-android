package co.keos.exampleapp.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.List;

import co.chatsdk.chat.ChatActionsCallback;
import co.chatsdk.chat.Message;
import co.chatsdk.firebase.generalfunc.PengiFunctions;
import co.keos.exampleapp.R;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * This class is the main chat activity, here is where the client and the agent will
 * match and they will talk
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 */
public class ChatActivity extends Activity{
    private static final String TAG = "ChatActivity";
    private EmojiconEditText textFieldChat;
    @SuppressLint("StaticFieldLeak")
    private static PengiFunctions chatFunctions;
    private boolean hold = false;
    private RecyclerView mMessageRecycler;
    private static MessageListAdapter mMessageAdapter;
    private static List<Message> messageList;
    private TextView welcomeContainer;
    private Activity mActivity;
    EmojiconTextView textView;
    View rootView;
    ImageView submitButton;
    ImageView emojiImageView;
    boolean isClosingChatOnPause;
    boolean isClosingChatOnDestroy;
    boolean chatLoad = false;
    Toolbar tb;
    int mainChatLayoutContainer;

    /**
     * Here we should setup the chat listeners and initialize the most important methods
     * @param savedInstanceState, Bundle with default value.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //The main view for the chat
        setContentView(R.layout.activity_message_list);

        //Initialize important values
        initAllVars();

        //Helps to init the Emoji compatibility
        initEmojis();

        //Sets the toolbar where the close chat action is.
        setActionBar(tb);

        //Start the chat callbacks
        chatListening();

        //Setups the send message click listener
        submitButton.setOnClickListener(view -> sendMsgChat());

        //Important for the view of messages appears
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
    }


    /**
     * Initialize most important variables for the activity
     */
    private void initAllVars() {
        mActivity = ChatActivity.this;
        mainChatLayoutContainer = R.id.allContainer;
        rootView = findViewById(mainChatLayoutContainer);
        textFieldChat = findViewById(R.id.edittext_chatbox);
        emojiImageView = findViewById(R.id.emoji_btn);
        submitButton = findViewById(R.id.submit_btn);
        textView = findViewById(R.id.text_message_body);
        tb = findViewById(R.id.toolbar);
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        welcomeContainer = findViewById(R.id.welcomeText);
        isClosingChatOnPause = false;
        isClosingChatOnDestroy = false;
        chatFunctions = (PengiFunctions) getIntent().getSerializableExtra("chatFunctions");
        messageList = chatFunctions.getChatMessages();
    }


    /**
     * Helps to initialize Emoji compatibility
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEmojis() {
        UtilsForApp.initEmojiCompat(mActivity);
        UtilsForApp.initEmojiIcons(mActivity, rootView, textFieldChat, emojiImageView);
    }


    /**
     * Helps to notify about main chat callback events. When chat loads, on incoming/outgoing message,
     * on chat is hold, when a transfer start, when an agent is found, when the connection is closed and
     * the connection is lost.
     */
    void chatListening(){
        chatFunctions.observeChat(new ChatActionsCallback() {

            /**
             * Notifies when the chat thread starts
             * @param welcomeMessage String, Welcome message from Pengi administrator
             * @param headMessage String, Head message managed from Pengi administrator
             */
            @Override
            public void onChatLoad(String welcomeMessage, String headMessage) {
                setInitialMessages(headMessage, welcomeMessage);
                chatLoad = true;
            }

            /**
             * Notifies when a message is added to the message list
             */
            @Override
            public void onMessageReceived() {
                synchronizeMessage();
            }

            /**
             * Notifies when agent put on hold or hold off the chat
             *
             * @param msg String, may contains 2 values holdStatusOn or holdStatusOff
             * @param holdConfigMessage String, Hold message managed from Pengi administrator
             */
            @Override
            public void onHold(String msg, String holdConfigMessage) {
                onHoldSwitch(msg,holdConfigMessage);
            }

            /**
             * Notifies when the agent starts a transfer to another agent
             */
            @Override
            public void turnOnProgress() {
                UtilsForApp.openPopUpWindow(mActivity, "Transferring in progress", mainChatLayoutContainer,false, false);
                chatLoad = false;
            }

            /**
             * Notifies when a new agents is found after start the transfer
             */
            @Override
            public void newUserAgentFound() {
                clearChat();
                UtilsForApp.closePopUpWindow();
            }

            /**
             * notifies when the user client or agent close the chat.
             * @param disconnectMessage String, this is the on close connection message managed from Pengi administrator.
             */
            @Override
            public void onCloseConnection(String disconnectMessage) {
                UtilsForApp.openPopUpWindow(mActivity, disconnectMessage, mainChatLayoutContainer, true,false);
                chatLoad = false;
            }

            /**
             * Notifies when the connection fail, maybe because the agent lost internet connection or something like that.
             * @param connectionFailMessage String, this is the on connection fail message managed from Pengi administrator.
             */
            @Override
            public void onConnectionFail(String connectionFailMessage) {
                UtilsForApp.openPopUpWindow(mActivity, connectionFailMessage, mainChatLayoutContainer, true,false);
                chatLoad = false;
            }
        });
    }


    /**
     * Helps to switch the behavior of the chat on Hold events
     *
     * @param msg String contains the two possible values to hold or not the chat.
     * @param holdConfigMessage String contains the hold On message.
     */
    void onHoldSwitch(String msg, String holdConfigMessage){
        switch (msg){
            case "holdStatusOn" :
                hold(true, holdConfigMessage);
                break;
            case  "holdStatusOff":
                hold(false, holdConfigMessage);
                break;
        }
    }


    /**
     * Helps to setup the hold On and hold Off events.
     * @param holdNow
     * @param holdMessage
     */
    void hold(boolean holdNow, String holdMessage){
        if(holdNow){
            hold=true;
            UtilsForApp.openPopUpWindow(mActivity,holdMessage, mainChatLayoutContainer,false, false);
        }else{
            hold=false;
            UtilsForApp.closePopUpWindow();
        }
    }


    /**
     * Helps to setup the initial messages to be used.
     *
     * @param headMessage String with the head message from default Pengi administrator messages.
     * @param welcomeMessage String with the welcome message from default Pengi administrator messages.
     */
    public void setInitialMessages(String headMessage, String welcomeMessage ){
        setTitle(headMessage);
        welcomeContainer.setText(welcomeMessage);
    }


    /**
     * Helps to show correctly the new messages on recycler view.
     */
    public void synchronizeMessage(){
        mMessageAdapter = new MessageListAdapter(messageList);
        mMessageRecycler.scrollToPosition(messageList.size() - 1);
        mMessageRecycler.setAdapter(mMessageAdapter);
    }


    /**
     * Helps to clean the message list in necessary case.
     */
    private static void clearChat() {
        if(chatFunctions.clearMessageList(messageList)){
            mMessageAdapter.notifyDataSetChanged(); //let your adapter know about the changes and reload view.
        }
    }


    /**
     * Helps to close the chat session, is like you exit from the chat.
     */
    public static void closeChat(){
        clearChat();
        chatFunctions.closeChat();
    }


    /**
     * Helps to validate that the message is not empty and sends the message.
     */
    public void sendMsgChat(){
        if(!textFieldChat.getText().toString().isEmpty()){
            chatFunctions.send(textFieldChat.getText().toString());
            textFieldChat.setText("");
        }
    }


    /**
     * Helps to install the toolbar
     *
     * @param menu the xml menu
     * @return true.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        tb.inflateMenu(R.menu.chatmenu);
        tb.setOnMenuItemClickListener(this::onOptionsItemSelected);
        return true;
    }


    /**
     * Helps to define the action of the menu items.
     *
     * @param item the MenuItem
     * @return true or default setting
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close_chat:
                UtilsForApp.openPopUpWindow(mActivity,"Are you sure that you want to close this chat session?",mainChatLayoutContainer,true,true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Notifies when the back button is pressed and helps in the chat process, if you want to close or if you are hold on.
     */
    @Override
    public void onBackPressed() {
        if(hold){
            Toast.makeText(getApplicationContext(), "Please wait!", Toast.LENGTH_SHORT).show();
        }else{
            UtilsForApp.openPopUpWindow(mActivity, "Do you want to close this chat session?", mainChatLayoutContainer,true,true);
        }
    }


    /**
     * Helps to unset the timer to kill the session when the user resume the chat.
     */
    @Override
    protected void onResume() {
        super.onResume();

        //if the chat has already loaded unset the kill conversation timer and the counter stops.
        if(chatLoad){
            chatFunctions.setTimerToKillConversation(false);
        }
    }


    /**
     * Helps to close the chat session when detects that the user wants to exit from the chat activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            //Flag to help to know when the user wants to close the chat
            isClosingChatOnPause = true;
            closeChat();
        }else{
            isClosingChatOnPause = false;
        }
    }


    /**
     * Helps to set the timer to kill the session when the user put on stop or minimize the app,
     * that will make the agent wait, very bad idea for the business, so the timer will start so on.
     */
    @Override
    protected void onStop() {
        super.onStop();

        if(!isFinishing()){
            //if the chat has already loaded set the kill conversation timer and the counter start
            if(chatLoad){
                chatFunctions.setTimerToKillConversation(true);
            }
        }
    }


    /**
     * Helps to know when the chat activity will be closed and kill the socket and close the chat.
     */
    @Override
    protected void onDestroy() {
        if(isFinishing()){
            isClosingChatOnDestroy = true;
            if(!isClosingChatOnPause){closeChat();}
        }else{
            isClosingChatOnDestroy = false;
        }
        super.onDestroy();
    }

}
