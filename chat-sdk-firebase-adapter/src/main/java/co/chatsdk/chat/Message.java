package co.chatsdk.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The class helps to manage messages that came from firebase room/chat.
 * When a message is registered on firebase this class is instantiated
 * and fill a list of messages for developers.
 *
 * @author <a href="mailto:danielduran@keos.co">Daniel Durán Schütz</a>
 * @version 1.0
 *
 */
public class Message{
    private static final String TAG = "Message";
    private static List<Message> messages = new ArrayList<>();

    private String channelChat;
    private String messageChat;
    private String nameChat;
    private String timeChat;


    /**
     * Class constructor. Creates an instance of class Message and fill the message list,
     * that is to be used on developers side.
     *
     * @param  channelChatC who is sending de message. Can be the client or the agent. Possible values are "cliente" or "agente"
     * @param  messageChatC the text message. Every text in the conversation.
     * @param  nameChatC the name of the sender.
     * @param  timeChatC in Milliseconds. When the message was wrote.
     */
    protected Message(String channelChatC, String messageChatC, String nameChatC, String timeChatC) {
        channelChat = channelChatC;
        messageChat = messageChatC;
        nameChat = nameChatC;
        timeChat = timeChatC;

        //Add message to the message list
        messages.add(this);
    }


    /**
     * getMessageList is a public getter for return the message list.
     * Call this method from UI to fill your adapters.
     *
     * @return an object of type List<Message>. Contain the list of every message.
     */
    public static List<Message> getMessageList() {
        return messages;
    }


    /**
     * getChannelChat is public getter for return the sender of the message.
     * @return an String with the descriptor of who is sending the message.
     */
    public String getChannelChat(){
        return channelChat;
    }


    /**
     * getMessageChat is a public getter for return the text of the message.
     * @return an String with the message content.
     */
    public String getMessageChat(){
        return messageChat;
    }


    /**
     * getNameChat is a public getter for return the name of the sender.
     * @return an String with the name of the user.
     */
    public String getNameChat(){
        return nameChat;
    }


    /**
     * getTimeChat is a public getter for return the datetime of the message when it was sent.
     * @return an String with the datetime of sent.
     */
    public String getTimeChat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = new Date(Long.parseLong(timeChat));
        return dateFormat.format(date);
    }

}
