package co.keos.exampleapp.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.chatsdk.chat.Message;
import co.keos.exampleapp.R;

/**
 * The class helps to set the values of the message objects to according view, in this case the received messages.
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 */
class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    private static final String TAG ="ReceivedMessageHolder";
    private TextView messageText, timeText, nameText;

    /**
     * Sets the views
     * @param itemView the xml layout for the received message
     */
    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
        nameText = itemView.findViewById(R.id.text_message_name);
    }

    /**
     * Set the values time, name and message to according views
     * @param message Message with the message object
     */
    void bind(Message message) {
        //Sets the message text to according view.
        messageText.setText(message.getMessageChat());

        // Format the stored timestamp into a readable String using method.
        timeText.setText(message.getTimeChat());

        //Sets the name of the sender
        nameText.setText(message.getNameChat());
    }
}