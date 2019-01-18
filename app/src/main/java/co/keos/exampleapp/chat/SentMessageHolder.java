package co.keos.exampleapp.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.chatsdk.chat.Message;
import co.keos.exampleapp.R;

/**
 * The class helps to set the values of the message objects to according view, in this case the sent messages.
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 */
class SentMessageHolder extends RecyclerView.ViewHolder {
    private static final String TAG ="SentMessageHolder";
    private TextView messageText, timeText;

    /**
     * Sets the views
     * @param itemView the xml layout for the sent message.
     */
    SentMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
    }

    /**
     * //Set the values of the time and the message to according views
     * @param message Message with the message object
     */
    void bind(Message message) {
        //Sets the message
        messageText.setText(message.getMessageChat());

        // Format the stored timestamp into a readable String using method.
        timeText.setText(message.getTimeChat());
    }
}