package co.keos.exampleapp.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import co.keos.exampleapp.R;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * This class helps with utilities methods to improve the user experience and order the source code
 * of the main app.
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 */
public class UtilsForApp{
    private static final String TAG = "UtilsForApp";
    private static PopupWindow mPopupWindow;
    private static Button closeButton;
    private static Activity activity;
    private static Button continueChatButton;


    /**
     * Setups the popup main config.
     *
     * @param context of type Activity, contains the activity where the popUp will be.
     * @param toShowOn of type ConstraintLayout in this case. This is the layout where the popUp
     *                 will be or the rootComponent.
     * @param popUpMessage String with the message that you want to show in the TextView of the PopUp.
     */
    private static void setUpPopUpConfig(Activity context, int toShowOn, String popUpMessage){
        activity = context;
        // Get the widgets reference from XML layout
        View layoutChat = activity.findViewById(toShowOn);

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popup_window, null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        closeButton = customView.findViewById(R.id.ib_close);
        continueChatButton = customView.findViewById(R.id.continuarButton);

        // Finally, show the popup window at the center location of root relative layout
        mPopupWindow.showAtLocation(layoutChat, Gravity.CENTER,0,0);
        TextView textViewPopUp = customView.findViewById(R.id.tv);
        textViewPopUp.setText(popUpMessage);

    }


    /**
     * Setups the close popUp button visibility and click listener.
     * @param navigateUp boolean, if true the user returns to the previous activity.
     */
    private static void setUpClosePopUpButton(boolean navigateUp){
        closeButton.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(view -> {
            closePopUpWindow();
            if(navigateUp){navigateUp(activity);}

        });
    }


    /**
     * Setups the continue button for PopUp and sets the click listener
     */
    private static void setContinuePopUpButton(){
        continueChatButton.setVisibility(View.VISIBLE);
        continueChatButton.setOnClickListener(v -> closePopUpWindow());
    }


    /**
     * This method helps in the open pop up event, install the popUp and sets if an exit button is
     * placed or not. This method is used in the mainActivity for standard messages and notifications.
     *
     * @param context The activity where the popUp will be shown.
     * @param popUpMessage The string message for the user.
     * @param toShowOn The layout component or root view.
     * @param showExitButton boolean, if true the close button is installed.
     */
    public static void openPopUpWindow(Activity context, String popUpMessage, int toShowOn, boolean showExitButton){
        setUpPopUpConfig(context,toShowOn,popUpMessage);
        if(showExitButton){
            setUpClosePopUpButton(false);
        }
    }

    /**
     * This method helps in the open pop up event, install the popUp and sets if an exit button is
     * placed or not and if we want to show the continue button-. This method is used when the chat is
     * in progress to show some default system messages.
     *
     * @param context The Actvity context where the popUp will be.
     * @param popUpMessage the string message to show
     * @param toShowOn int, The layout root component.
     * @param navigateUp boolean, if the user came back to previous activity or not. Used when the
     *                   chat is closed
     * @param isCloseChatAction boolean, if the user wants to close the chat.
     */
    public static void openPopUpWindow(Activity context, String popUpMessage, int toShowOn, boolean navigateUp, boolean isCloseChatAction){
        setUpPopUpConfig(context,toShowOn, popUpMessage);
        if(isCloseChatAction){
            setContinuePopUpButton();
        }
        if(navigateUp){
            setUpClosePopUpButton(true);
        }
    }

    /**
     * Close the popUp component using mPopupWindow.
     */
    public static void closePopUpWindow(){
        mPopupWindow.dismiss();
    }

    /**
     * Method used to come back to previous activity
     * @param context Activity, the previous activity to go.
     */
    private static void navigateUp(Activity context) {
        final Intent upIntent = NavUtils.getParentActivityIntent(context);
        if (NavUtils.shouldUpRecreateTask(context, upIntent) || context.isTaskRoot()) {
            TaskStackBuilder.create(context).addNextIntentWithParentStack(upIntent).startActivities();
        } else {
            NavUtils.navigateUpTo(context, upIntent);
        }
    }

    /**
     * Init the Emoji compatibility
     * @param context the Activity context. Should be the main chat activity
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initEmojiCompat(Activity context) {
        final EmojiCompat.Config config;
        config = new BundledEmojiCompatConfig(context);
        config.setReplaceAll(true)
                .registerInitCallback(new EmojiCompat.InitCallback() {
                    @Override
                    public void onInitialized() {
                        Log.e("appflow", "EmojiCompat onInitialized::" + TAG);
                    }

                    @Override
                    public void onFailed(@Nullable Throwable throwable) {
                        Log.e("appflow", "EmojiCompat onFailed:: " + throwable + " " + TAG);
                    }
                });
        EmojiCompat.init(config);
    }

    /**
     * Setups the functionality to get more icons and improve the user experience for that.
     *
     * @param context The Activity context. Should be the main chat activity.
     * @param rootView The Layout component root view.
     * @param textFieldChat the TextView of type EmojiconEditText from the library Emojicon.
     * @param emojiImageView ImageView that will be working like button to show the icons panel.
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void initEmojiIcons(Activity context, View rootView, EmojiconEditText textFieldChat, ImageView emojiImageView){
        EmojIconActions emojIcon;
        emojIcon = new EmojIconActions(context, rootView, textFieldChat, emojiImageView);
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {}

            @Override
            public void onKeyboardClose() {}
        });

        emojiImageView.setOnTouchListener((view, motionEvent) -> {
            // Open and show the SuperNova-Emoji keyboard.
            emojIcon.ShowEmojIcon();
            return false;
        });
        emojiImageView.setOnClickListener(view -> emojIcon.ShowEmojIcon());

    }

}
