package co.keos.exampleapp.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import co.chatsdk.chat.Message;
import co.keos.exampleapp.R;


/**
 * This class helps with the chat messages and place these where correspond according the
 * view and the holder.
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 */
public class MessageListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "MessageListAdapter";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<Message> mMessageList;

    /**
     * Class constructor. sets the messages list
     * @param messageList List<Message> with the messages
     */
    MessageListAdapter(List<Message> messageList) {
        mMessageList = messageList;
    }

    /**
     * Get the total of messages
     * @return int message quantity
     */
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    /**
     * Determines the appropriate ViewType according to the sender of the message.
     *
     * @param position int, the item position number
     * @return int with the kind of message
     */
    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        if (message.getChannelChat().equals("cliente")) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    /**
     * Inflates the appropriate layout according to the ViewType.
     *
     * @param parent ViewGroup parent view with default value.
     * @param viewType int with the kind of view defined.
     * @return the agreed MessegeHolder (Sent or Received).
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    /**
     * Passes the message object to a ViewHolder so that 1the contents can be bound to UI.
     *
     * @param holder of type RecyclerView.ViewHolder with the holder
     * @param position int, to be used for the message position.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

}