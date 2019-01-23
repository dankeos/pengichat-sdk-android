package co.chatsdk.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Objects;
import co.chatsdk.firebase.FirebasePaths;
import co.chatsdk.firebase.generalfunc.PengiAuthorizationConfig;
import co.chatsdk.firebase.generalfunc.PengiFunctions;
import co.chatsdk.firebase.generalfunc.UserAgentAvailableConfig;


/**
 * The class helps to manage all functions to listen for every event on firebase client socket,
 * Chat Rooms and status of the agent in a room (Logged true or false)-. Even this class manage
 * the main thread of the chat and the most important functions like send a message,
 * reconnect for transfer, notify hold, close chat connection, notify transfer, notify every message,
 * set response when losing connection, set some config messages.
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 *
 */
public class ChatController {
    public static final String TAG = "ChatController";

    //Campaign configuration
    private PengiAuthorizationConfig campConfig;

    //Client name
    private static String clientName;

    //Room id.
    private static String room;

    //Welcome default message
    private String welcomeMessage;

    //Bye Message default message
    private String byeMessage;

    //Disconnect Message default message
    private String disconnectMessage;

    //Hold Message default message
    private String holdMessage;

    //Head Message default message
    private String headMessage;

    //User agent configuration
    private UserAgentAvailableConfig userAgentConfig;

    //Consultant Token
    private String consultantToken;

    //indicates when the user is closing the app
    private boolean isExitingNow = false;


    /**
     * Class constructor. Initialize a chat instance that control the main functions of the chat session.
     * Set configurations and start listeners for the firebase connection.
     *
     * @param AuthConfig Object with all authentication and campaign configuration.
     * @param roomRemote String with the id of the room to be used.
     * @param consultantName String with the name of consultant (client name).
     * @param userAgentConfigFrom Object with all available UserAgent (agent) data.
     * @param consultantTokenFrom String with consultant ID. To be used somewhere
     * @param callback Object to notify developer of chat main actions.
     */
    public ChatController(PengiAuthorizationConfig AuthConfig, String roomRemote, String consultantName, UserAgentAvailableConfig userAgentConfigFrom, String consultantTokenFrom, ChatActionsCallback callback){
        campConfig = AuthConfig;
        clientName = consultantName;
        room = roomRemote;
        userAgentConfig = userAgentConfigFrom;
        consultantToken = consultantTokenFrom;

        setAllConfigMessages();
        listenForChat(callback);
        listenForRoomPersistenceAgent(callback);
        listenForSocketConsultant(callback);
    }


    /**
     * setAllConfigMessages private void helps to set some config messages to variables
     * (timer message is not included). The messages are retrieved thanks to
     * PengiAuthorizationConfig class and his method getMessages_config().
     */
    private void setAllConfigMessages(){
        welcomeMessage = campConfig.getMessages_config("bienvenida").replace("\"", "");
        byeMessage = campConfig.getMessages_config("despedida").replace("\"", "");
        disconnectMessage = campConfig.getMessages_config("desconexion").replace("\"", "");
        holdMessage = campConfig.getMessages_config("hold").replace("\"", "");
        headMessage = campConfig.getMessages_config("encabezado").replace("\"", "");
    }


    /**
     * listenForChat private void helps to notify when the room/chat node on firebase receives a message.
     * Some config messages are notified through ChatActionsCallback onChatLoad() function too.
     * Notify developers that the chat begins and for every incoming/outgoing message.
     *
     * @param chatCallBack ChatActionsCallback object to notify developers
     *                     when the chat loads with the method onChatLoad() and the chat instance
     *                     is listening for incoming/outgoing messages.
     */
    private void listenForChat(ChatActionsCallback chatCallBack){
        DatabaseReference chatref = FirebasePaths.threadRef(room + "/chat");
        chatCallBack.onChatLoad(welcomeMessage,headMessage);
        chatref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                sendMessageToDeveloper(dataSnapshot,chatCallBack);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot){}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey){}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }


    /**
     * sendMessageToDeveloper private void get every message from room/chat node when it is added and sends
     * notification to developer, creates a new instance of Message class that add the message
     * to a List<Message> object.
     * Notify to developers when a new message is added through ChatActionsCallback.
     *
     * @param dataSnapshot DataSnapshot object with node added on firebase and all attributes.
     * @param chatCallBack ChatActionsCallback with onMessageReceived() method helps to notify
     *                     developers of new message received.
     */
    private void sendMessageToDeveloper(DataSnapshot dataSnapshot, ChatActionsCallback chatCallBack){
        //String actionChat = dataSnapshot.child("action").getValue().toString();
        String channelChat = Objects.requireNonNull(dataSnapshot.child("channel").getValue()).toString();
        String messageChat = Objects.requireNonNull(dataSnapshot.child("message").getValue()).toString();
        String nameChat = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
        String timeChat =  Objects.requireNonNull(dataSnapshot.child("time").getValue()).toString();
        new Message(channelChat, messageChat, nameChat, timeChat);
        chatCallBack.onMessageReceived();
    }


    /**
     * listenForRoomPersistenceAgent private void listen for an agent left a room unexpectedly over
     * logged field if is true or false.
     * This is used for close the connection when the agent lose connection.
     *
     * @param chatCallBack of type ChatActionsCallback is passed to checkForLoggedAgent()
     *                     function that is used to check if userAgent is logged or not.
     */
    private void listenForRoomPersistenceAgent(ChatActionsCallback chatCallBack){
        DatabaseReference roomPersistenceAgent = FirebasePaths.threadRef(room + "/persistence/" + userAgentConfig.getId());
        roomPersistenceAgent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                checkForLoggedAgent(dataSnapshot,chatCallBack);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }


    /**
     * checkForLoggedAgent private void verify for a logged agent
     * through logged attribute found in room/persistence/userAgentId node if it is true or false.
     *
     * @param dataSnapshot contains the firebase called node with attributes.
     * @param chatCallBack of type ChatActionsCallback is used to notify developers through
     *                     onConnectionFail() method that the userAgent left unexpectedly
     *                     the session or lose connection.
     */
    private void checkForLoggedAgent(DataSnapshot dataSnapshot, ChatActionsCallback chatCallBack){
        DataSnapshot userAgentRoom = dataSnapshot.child("user");
        if(userAgentRoom.hasChild("logged")){
            Boolean isUserAgentLogged = Boolean.valueOf(String.valueOf(userAgentRoom.child("logged").getValue()));
            if(!isUserAgentLogged){
                if(!isExitingNow){
                    closeConnection();
                    chatCallBack.onConnectionFail(disconnectMessage);
                    isExitingNow = false;
                }
            }
        }
    }


    /**
     * listenForSocketConsultant private void listen for nodes added on sockets/userAgentToken/consultantToken
     * and helps to notify for Hold status, transfers or disconnect events.
     *
     * @param chatCallBack type ChatActionsCallback contain main events to notify developers for hold,
     *                     transfers or disconnect events.
     */
    private void listenForSocketConsultant(ChatActionsCallback chatCallBack){
        DatabaseReference socketConsultantRef = FirebasePaths.userRef(userAgentConfig.getToken_agent() + "/" + consultantToken);
        socketConsultantRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s){
                String _action_ = Objects.requireNonNull(dataSnapshot.child("_action_").getValue()).toString();
                DataSnapshot msgData = (dataSnapshot.hasChild("msg"))? dataSnapshot.child("msg") :null;
                String msg = (dataSnapshot.hasChild("msg"))?dataSnapshot.child("msg").getValue().toString():null;

                onSendHold(_action_,chatCallBack, msg);
                onTransfer(_action_, msgData, chatCallBack);
                onDisconnect(_action_,chatCallBack);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot){}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }


    /**
     * onSendHold private void helps to validate if _action_ is notifyHoldConsultant and notify to
     * developers of onHold event to be managed.
     *
     * @param _action_ String contains the action registered on firebase, the expected value may variate but
     *                 notifyHoldConsultant is the expected to be verified on developers side.
     * @param chatCallBack of type ChatActionsCallback, contains callback method to set onHold
     *                     notification for developers.
     * @param msg String contains values for notifyHoldConsultant events, expected values are
     *            holdStatusOn or holdStatusOff.
     */
    private void onSendHold(String _action_, ChatActionsCallback chatCallBack, String msg){
        if(_action_.equals("notifyHoldConsultant")) chatCallBack.onHold(msg, holdMessage);
    }


    /**
     * onTransfer private void helps to notify when a transfer is enabled in the session through
     * notifyTurnConsultant event.
     *
     * @param actionFrom String contains the event in deed. In this case may have a value notifyTurnConsultant
     *                   for transfers.
     * @param msgDataFrom DataSnapshot object contains the msg with transfer status.
     * @param chatCallBackFrom of type ChatActionsCallback contains the callback functions to notify developers
     *                         about transfer status. Values can be turnOnProgress or newUserAgentFound.
     */
    private void onTransfer(String actionFrom, DataSnapshot msgDataFrom, ChatActionsCallback chatCallBackFrom){
        if(actionFrom.equals("notifyTurnConsultant")){

            switch (msgDataFrom.child("name").getValue().toString()) {
                case "turnOnProgress":
                    chatCallBackFrom.turnOnProgress();
                    break;
                case "newUserAgentFound":
                    chatCallBackFrom.newUserAgentFound();
                    reconnect(msgDataFrom, chatCallBackFrom);
                    break;
                default:
                    Log.d(TAG, "onTransfer notifyTurnConsultant default switch::::" + msgDataFrom.child("name").getValue().toString());
                    break;
            }

        }
    }


    /**
     * onDisconnect private void notify when the event is desconexion is set. Use closeConnection
     * to help close the chat session.
     *
     * @param _action_ String may contain the event value for disconnect session.
     *                 Value may be desconexion.
     * @param chatCallBack of type ChatActionsCallback with method onCloseConnection used for notify
     *                     about close event and send byeMessage for developers take advantage.
     */
    private void onDisconnect(String _action_, ChatActionsCallback chatCallBack){
        if(_action_.equals("desconexion")){
            closeConnection();
            chatCallBack.onCloseConnection(byeMessage);
        }
    }


    /**
     * sendMessage public void inserts the message in the room/chat firebase node.
     * @param messageFrom String include the message content and it is published on the message attribute.
     */
    public void sendMessage(String messageFrom){
        HashMap<String, Object> message = new HashMap<>();
        message.put("action", "message");
        message.put("channel", "cliente");
        message.put("message", messageFrom);
        message.put("name", clientName);
        message.put("time", System.currentTimeMillis());
        FirebasePaths.threadRef(room + "/chat").push().setValue(message);
    }


    /**
     * closeConnection public void close the chat session removing the sockets/userAgentToken/consultantToken node
     * and then goOffline for the firebase instance. Sets isExitingNow to true.
     */
    public void closeConnection(){
        isExitingNow = true;
        FirebasePaths.userRef(userAgentConfig.getToken_agent() + "/"+consultantToken).getRef().removeValue();
        FirebaseDatabase.getInstance().goOffline();
    }


    /**
     * reconnect private void is used when the transfer is enabled. Get the new room, tokenAgent,
     * idAgent and sends data to the PengiFunctions.transferConsultant function.
     *
     * @param dataFromMsg of type DataSnapshot contains the node info with important attributes that
     *                    are being used to send through the transfer function.
     * @param callback of type ChatActionsCallback helps to notify developers about transfer events.
     *                 This callback is a param for the transferConsultant function.
     */
    private void reconnect(DataSnapshot dataFromMsg, ChatActionsCallback callback){
        DataSnapshot agentData = dataFromMsg.child("agentData");
        DataSnapshot msgAgentData = agentData.child("msg");
        String tokenAgent = Objects.requireNonNull(msgAgentData.child("token_internal").getValue()).toString();
        String random_room = Objects.requireNonNull(msgAgentData.child("random_room").getValue()).toString();
        int idAgent = Integer.parseInt(Objects.requireNonNull(msgAgentData.child("id").getValue()).toString());
        closeConnection();
        PengiFunctions.transferConsultant(tokenAgent,random_room,idAgent,callback);
    }

}
