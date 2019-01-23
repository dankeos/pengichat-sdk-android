package co.chatsdk.firebase.generalfunc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import co.chatsdk.chat.ChatActionsCallback;
import co.chatsdk.chat.ChatController;
import co.chatsdk.chat.Message;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.firebase.FirebasePaths;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * The class helps to initialize main functions of chat that being acceded and managed for developers
 * in their Chat classes. This class is related to ChatSdk authentication, Pengi endpoint services
 * and ChatController connection. Contains functions to make relationships with service endpoints
 * that connect the Pengi userAgent and UserClient SDK.
 * Contains functions that will be used for developers in their MainActivities / ChatActivities classes.
 * Implements Serializable for PengiFunctions object.
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 *
 */
public class PengiFunctions implements Serializable {
    private static final String TAG= "PengiFunctions";

    //Auhentication String Token
    private static String authToken;

    //App Id
    private String originApp;

    //User client name
    private static String name;

    //User client email
    private static String userEmail;

    //User client string Location
    private static String location;

    //PengiAuthorizationConfig Object with Campaign configuration
    private static PengiAuthorizationConfig authConfig;

    //UserAgentAvailableConfig Object with the User Agent configuration
    private static UserAgentAvailableConfig userAgentConfig;

    //Consultant Configuration
    private static CreateConsultantConfig consultantConfig;

    //Agent token
    private static String tokenAgent;

    //Consultant token
    private static String consultantToken;

    //Internal code
    private static String _internal_;

    //Room id
    private static String room;

    //Consultant ID
    private static int consultantId;

    @SuppressLint("StaticFieldLeak")

    // Context
    private static Context context;

    //Campaign ID.
    private static int campaignId;

    //ChatController instance
    public static ChatController chat;

    //Searching agents trials
    private static int getAgentTrials = 0;


    /**
     * Class constructor. Initialize the PengiFunction object to be used on developers side.
     * This constructor call for getPengiAuthorization method that retrieves the main authentication
     * to use CHAT SDK correctly.
     *
     * @param nameUser String contains the name of the user client.
     * @param userMail String contains the email of the user client.
     * @param locationFrom String contains the user client location sent by developers in String format
     *             and easy to read, for example "Bogota, Colombia".
     * @param token String contains the unique token authentication provided by Keos exclusively
     *              to be used for a developer with one AppID and one campaign.
     * @param originAppFrom String contains the unique AppID provided by developer and validated for
     *                   Pengi Authentication Service.
     * @param contextFrom of type Context that contains the MainActivity where the chat button stands.
     * @param callback of type PengiAuthCallback contains the callback to notify developers for most
     *                 important and initial callback method for authentication.
     */
    public PengiFunctions(String nameUser, String userMail, String locationFrom, String token, String originAppFrom, Context contextFrom, PengiAuthCallback callback ){
        name  = nameUser;
        userEmail = userMail;
        location = locationFrom;
        authToken = token;
        originApp = originAppFrom;
        context = contextFrom;
        getPengiAuthorization(callback);
    }


    /**
     * Private void getPengiAuthorization helps to get authorization from Pengi system and get all
     * campaign configuration thanks to PengiAuthorizationConfig class.
     *
     * @param callBackNow of type PengiAuthCallback helps to notify developers about response authentication
     */
    private void getPengiAuthorization(PengiAuthCallback callBackNow) {
        getAuthorizationCall().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                validatePengiAuthorizationResponse(response, callBackNow);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error",t.getMessage());
                callBackNow.onCallFailure();
            }
        });
    }


    /**
     * Private static void getUserAgentAvailable helps to get an userAgent available from Pengi ACD endpoint.
     * When the userAgent is available the next events will connect the user client with the agent.
     *
     * @param callback of type PengiAgentAvailableCallback have functions to notify developers about
     *                 retrieve agents response, the main callbacks that being used are:  onAgentAvailable,
     *                 onCallFailure and noAgentsAvailable.
     */
    private static void getUserAgentAvailable(PengiAgentAvailableCallback callback){
        if(authConfig.isOpen){
            callback.onSearchAgent();
            getUserAgentCall().enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    validateGetUserAgentResponse(response,callback);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("Error",t.getMessage());
                    callback.onCallFailure();
                }
            });
        }else{
            callback.onUnavailableSchedule(authConfig.getMessages_config("timer").replace("\"", ""));
        }
    }


    /**
     * Private static void createConsultant helps to create a consultant when the
     * CreateConsultantService response success.
     */
    private static void createConsultant(){
        createConsultantCall().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                validateCreateConsultantResponse(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error",t.getMessage());
            }
        });
    }


    /**
     * Private static void createRelationAgentConsultant creates a relationship between userAgent and
     * consultantUser through CreateRelationAgentConsultantService and this make changes in database.
     *
     * @param idConsultant int contains the consultant Id.
     * @param idAgent int contains the agent id.
     */
    private static void createRelationAgentConsultant(int idConsultant, int idAgent){
        createRelationAgentConsultantCall(idConsultant,idAgent).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                validateCreateRelationAgentConsultant(response,idConsultant);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error",t.getMessage() +  " " + TAG);
            }
        });
    }


    /**
     * Private static void updateTokenChatConsultant sets important values for the
     * updateTokenChatConsultantService endpoint that update Pengi database.
     *
     * @param consultantId int contains the consultant ID.
     * @param tcOrRandomRoom String contains the room ID.
     */
    private static void updateTokenChatConsultant(int consultantId, String tcOrRandomRoom){
        updateTokenChatConsultantCall(consultantId,tcOrRandomRoom).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                validateUpdateTokenChatConsultant(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error",t.getMessage() + " " +TAG);
            }
        });
    }


    /**
     * Private Call<JsonObject> getAuthorizationCall() makes the call to authentication endpoint.
     * @return Call<JsonObject> with response. Normally will be the campaign configuration object.
     */
    private Call<JsonObject> getAuthorizationCall(){
        PengiAuthorizationService pengiAuthorizationService = Configuration.retrofit.create(PengiAuthorizationService.class);
        return pengiAuthorizationService.getData(authToken, originApp);
    }


    /**
     * Private static Call<JsonObject> getUserAgentCall makes the call to getUserAgentAvaliable endpoint.
     * @return Call<JsonObject> with response. May be UserAgent data object.
     */
    private static Call<JsonObject> getUserAgentCall(){
        UserAgentAvailableService userAgentAvailableService = Configuration.retrofit.create(UserAgentAvailableService.class);
        return userAgentAvailableService.getData("2", campaignId);
    }


    /**
     * Private static Call<JsonObject> createConsultantCall makes the call to createConsultant endpoint.
     * @return Call<JsonObject> with response. May be the consultant ID.
     */
    private static Call<JsonObject> createConsultantCall(){
        CreateConsultantService createConsultantService = Configuration.retrofit.create(CreateConsultantService.class);
        return createConsultantService.getData(name,userEmail,location,"[]","","",authToken,"pw_co","null",tokenAgent,room);
    }


    /**
     * Private static Call<JsonObject> createRelationAgentConsultantCall makes the call to createRelationAgentConsultant endpoint.
     * @param idConsultant int, the consultant ID.
     * @param idAgent int, the agent ID.
     * @return Call<JsonObject> with response. No special value for response in the callbacks
     */
    private static Call<JsonObject> createRelationAgentConsultantCall(int idConsultant, int idAgent){
        CreateRelationAgentConsultantService createRelationAgentConsultantService = Configuration.retrofit.create(CreateRelationAgentConsultantService.class);
        return createRelationAgentConsultantService.getData(idConsultant,idAgent);
    }


    /**
     * Private static Call<JsonObject> updateTokenChatConsultantCall makes the call to updateTokenChatConsultant endpoint.
     * @param consultantId int, the consultant ID.
     * @param tcOrRandomRoom String, the room unique id.
     * @return Call<JsonObject>  with response. No special value for response in the callbacks
     */
    private static Call<JsonObject> updateTokenChatConsultantCall(int consultantId, String tcOrRandomRoom){
        UpdateTokenChatConsultantService updateTokenChatConsultantService = Configuration.retrofit.create(UpdateTokenChatConsultantService.class);
        return updateTokenChatConsultantService.getData(consultantId,tcOrRandomRoom);
    }


    /**
     * Private void validatePengiAuthorizationResponse validates the response of getAuthorizationCall
     * if it was success a new instance of PengiAuthorizationConfig is created and a notification is
     * send to developer (onAuthenticationSuccess); otherwise may response to developer onCallFailure
     * or onAuthenticationFail.
     *
     * @param response Response<JsonObject> with nodes and attributes of campaign configuration.
     * @param callBackNow of type PengiAuthCallback helps for notify to developers with callback methods
     *                    onAuthenticationSuccess,onCallFailure,onAuthenticationFail.
     */
    private void validatePengiAuthorizationResponse(Response<JsonObject> response, PengiAuthCallback callBackNow){
        if(response.code() == 200){
            if(response.body().has("status")){
                String responseStatus = response.body().get("status").toString();
                if(responseStatus.equals("\"success\"")){
                    authConfig = new PengiAuthorizationConfig(response);
                    campaignId = authConfig.getCampaign_id();
                    callBackNow.onAuthenticationSuccess();
                }else{
                    callBackNow.onAuthenticationFail();
                }
            }else{
                callBackNow.onAuthenticationFail();
            }
        }else{
            callBackNow.onCallFailure();
        }
    }


    /**
     * Private static void validateGetUserAgentResponse validates the response of getUserAgentCall that
     * will retrieve userAgent configuration. Creates a new instance of UserAgentAvailableConfig to get
     * tokenAgent and the room. Helps to set user client online, notify the developer with onAgentAvailable and
     * helps to create a consultant. If agents are unavailable will help to get an agent with some trials more.
     *
     * @param response Response<JsonObject> with userAgent configuration, room and other attributes.
     * @param callback of type PengiAgentAvailableCallback has been used to notify developers with
     *                 onAgentAvailable or noAgentsAvailable in retryReclaimAgent function.
     */
    private static void validateGetUserAgentResponse(Response<JsonObject> response, PengiAgentAvailableCallback callback){
        String responseStatus = response.body().get("status").toString();
        if(responseStatus.equals("\"ok\"")){
            userAgentConfig = new UserAgentAvailableConfig(response);
            tokenAgent = userAgentConfig.getToken_agent();
            room = userAgentConfig.getRandom_room();
            goOnline(tokenAgent);
            callback.onAgentAvailable();
            createConsultant();
        }else{
            if(responseStatus.equals("\"fail\"")){
                retryReclaimAgent(callback);
            }
        }
    }


    /**
     * Private static void validateCreateConsultantResponse validates the response of createConsultant endpoint.
     * If success creates an instance of CreateConsultantConfig and helps to create important nodes in firebase
     * socket, sets the room and helps to create the userAgent and consultant relationship.
     *
     * @param response Response<JsonObject>, this response may contain the consultant ID.
     */
    private static void validateCreateConsultantResponse(Response<JsonObject> response){
        String responseStatus = response.body().get("status").toString();
        if(responseStatus.equals("\"success\"")){
            consultantConfig = new CreateConsultantConfig(response);
            consultantId = consultantConfig.getId();
            setUserInfoData(tokenAgent);
            setRoom(room);
            createRelationAgentConsultant(consultantId, userAgentConfig.getId());
        }else{
            Log.e("appflow", "validateCreateConsultantResponse NO success: " + responseStatus + " " +TAG);
        }
    }


    /**
     * Private static void validateCreateRelationAgentConsultant validates the response of
     * createRelationAgentConsultant endpoint. Helps to set the consultant in firebase rooms/roomid node.
     * Helps to update the consultant token in database.
     *
     * @param response of type Response<JsonObject>, is used only to get the response status value
     * @param idConsultant int. The consultant ID.
     */
    private static void validateCreateRelationAgentConsultant(Response<JsonObject> response,int idConsultant){
        String responseStatus = response.body().get("status").toString();
        if(responseStatus.equals("\"success\"")){
            setConsultantToRoom(idConsultant, true, false, "chat",room);
            updateTokenChatConsultant(idConsultant, room);
        }else{
            Log.e("appflow", "validateCreateRelationAgentConsultant status NO success:: " + responseStatus + " " +TAG);
        }
    }


    /**
     * Private static void validateUpdateTokenChatConsultant only receive the response to follow the
     * status attribute, if it is success or not.
     *
     * @param response of type Response<JsonObject>, used to follow the status if the request.
     */
    private static void validateUpdateTokenChatConsultant(Response<JsonObject> response){
        String responseStatus = response.body().get("status").toString();
        if(responseStatus.equals("\"success\"")){
            Log.d("appflow", "validateUpdateTokenChatConsultant success" + TAG);
        }else{
            Log.e("appflow", "validateUpdateTokenChatConsultant no success - status"+ responseStatus + " " + TAG);
        }
    }


    /**
     * Private static void retryReclaimAgent helps to retry the request of an agent. If are not
     * agents available a functions is called to notify developers noAgentsAvailable.
     *
     * @param callback of type PengiAgentAvailableCallback contains a callback method noAgentsAvailable that is used to
     *                 notify developer.
     */
    private static void retryReclaimAgent(PengiAgentAvailableCallback callback){
        int countGetAgent = 9000;
        for (int e = 0; e <= countGetAgent; e++){
            if(e == countGetAgent){
                getAgentTrials = getAgentTrials + 1;
                if(getAgentTrials == 7){
                    getAgentTrials = 0;
                    callback.noAgentsAvailable();
                }else{
                    getUserAgentAvailable(callback);
                }
            }
        }
    }


    /**
     * private static void goOnline sets the user client ready and online for the agent in firebase
     * node /sockets/tokenAgent/consultantToken/
     *
     * @param token String, contains the userAgent token
     */
    private static void goOnline(String token) {
        consultantToken = UUID.randomUUID().toString();
        _internal_ = UUID.randomUUID().toString().substring(0,8);
        HashMap<String, Object> map = new HashMap<>();
        map.put("_action_", "_init_");
        map.put("_internal_", _internal_);
        map.put("consultantToken", consultantToken);
        FirebasePaths.userRef(token).child(consultantToken).push().setValue(map);
    }


    /**
     * private static void setUserInfoData sets the user client info in firebase node
     * /sockets/tokenAgent/consultantToken
     *
     * @param token String, userAgent Token.
     */
    private static void setUserInfoData(String token){
        HashMap<String, Object> UserInfo = new HashMap<>();
        UserInfo.put("email", userEmail);
        UserInfo.put("id", consultantConfig.getId());
        UserInfo.put("name", name);
        UserInfo.put("originToken", authToken);
        UserInfo.put("other_params", "[]");
        UserInfo.put("random_room",userAgentConfig.getRandom_room());
        UserInfo.put("token_peer","null");
        UserInfo.put("ubication",location);

        HashMap<String, Object> data = new HashMap<>();
        data.put("UserInfo", UserInfo);
        data.put("_action_", "notifyToUserAgent");
        data.put("_internal_", _internal_);
        data.put("campaign_id", campaignId);
        data.put("channel_id", 2);
        data.put("consultantToken", consultantToken);
        data.put("extensionConference", "");
        data.put("tokenUserAgent", token);
        data.put("ubication", location);
        FirebasePaths.userRef(token).child(consultantToken).push().setValue(data);
    }


    /**
     * private static void setRoom sets the room in firebase node /rooms
     * @param roomID String, The id of the room
     */
    private static void setRoom(String roomID){
        FirebasePaths.threadRef(roomID);
    }


    /**
     * private static void setConsultantToRoom sets some consultant info to a firebase node
     * /rooms/roomId/persistence/
     *
     * @param consultantId int, Consultant id.
     * @param logged boolean, if the user is logged the value is true, if not value is false
     * @param owner boolean, the client is owner false, the agent is owner true (owner of the room)
     * @param type String, the value is always "chat"
     * @param newRoom String, contains the id of the room. This will be the room where the consultant
     *                will be placed.
     */
    private static void setConsultantToRoom(int consultantId, boolean logged, boolean owner, String type, String newRoom){
        HashMap<String, Object> consultantDataRoom = new HashMap<>();
        consultantDataRoom.put("id", consultantId);
        consultantDataRoom.put("logged", logged);
        consultantDataRoom.put("owner", owner);
        consultantDataRoom.put("type", type);
        HashMap<String, Object> dataFinal = new HashMap<>();
        dataFinal.put("user", consultantDataRoom);
        FirebasePaths.threadRef(newRoom + "/persistence").child(String.valueOf(consultantId)).setValue(dataFinal);
    }


    /**
     * public void setTimerToKillConversation sends the event to the firebase node
     * /sockets/tokenAgent/consultantToken/ and will keep informed to the agent that the user client
     * maybe kill the app setting an _action_ setTimerToKillConversation and with the attribute "kill"
     * with value true, if the app is not killed the value for "kill attribute is false.
     * Always the agent is notified about this event for closing chat or not-. showKillWarning notify
     * to the user client about the event.
     *
     * @param killFrom boolean, if the app will be killed the value always is true, if not is false
     */
    public void setTimerToKillConversation(boolean killFrom){
        HashMap<String, Object> map = new HashMap<>();
        map.put("_action_", "setTimerToKillConversation");
        map.put("id_consultant", consultantId );
        map.put("tokenConsultant", consultantToken );
        map.put("_internal_", _internal_);
        map.put("timeInSeconds", ChatSDK.config().timeToKillChat);
        map.put("room", getRoom());
        map.put("kill",killFrom );
        FirebasePaths.userRef(tokenAgent).child(consultantToken).push().setValue(map);
        showKillWarning(killFrom);
    }


    /**
     * private static void showKillWarning notifies user client about the action of kill or not de app.
     * @param killFrom boolean, will be true if the app will be killed and false if not.
     */
    private static void showKillWarning(boolean killFrom){
        if(killFrom){
            Toast.makeText(context, "The connection will be closed at " + ChatSDK.config().timeToKillChat + " seconds", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, "Connection successful", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * public static void transferConsultant helps to transfer an user client to another agent.
     * Resume the connection first, set all needed config to log the user client at the new room,
     * creates a new instance of ChatController for the correct use. This function makes posible that
     * the client continue on chat with the other agent.
     *
     * @param tokenAgent String, new token agent id.
     * @param nuevaRoom String, the new room id to transfer the client.
     * @param idAgente int, new agent Id.
     * @param callback ChatActionsCallback, makes posible notify developers about main Chat Actions.
     */
    public static void transferConsultant(String tokenAgent, String nuevaRoom,int idAgente, ChatActionsCallback callback){
        resumeConnection();
        room = nuevaRoom;
        userAgentConfig.setToken_agent(tokenAgent);
        userAgentConfig.setRandom_room(nuevaRoom);
        userAgentConfig.setId(idAgente);
        goOnline(tokenAgent);
        setUserInfoData(tokenAgent);
        setRoom(nuevaRoom);
        chat = new ChatController(authConfig, nuevaRoom, name, userAgentConfig, consultantToken, callback);
        setConsultantToRoom(consultantId, true, false, "chat",nuevaRoom);
        updateTokenChatConsultant(consultantId, nuevaRoom);
    }


    /**
     * public void send helps to send the message written for the client.
     * @param text String, the message content.
     */
    public void send(String text){
        chat.sendMessage(text);
    }


    /**
     * public void searchForAgents helps to get or search an agent.
     * @param callback PengiAgentAvailableCallback, notify about callback functions when the userAgent
     *                 was found or not.
     */
    public void searchForAgents(PengiAgentAvailableCallback callback){
        getUserAgentAvailable(callback);
    }


    /**
     * public void observeChat creates the initial instance of ChatController.
     * @param callback ChatActionsCallback helps to notify developers about the main actions of the chat
     */
    public void observeChat(ChatActionsCallback callback){
        chat = new ChatController(authConfig, room, name,userAgentConfig, consultantToken, callback);
    }


    /**
     * public static void resumeConnection gets the instance of Firebase and force to go online again.
     * It is used when an user client is transferred or when the chat is closed and the connection is
     * resumed.
     */
    public static void resumeConnection(){
        FirebaseDatabase.getInstance().goOnline();
    }


    /**
     * public boolean clearMessageList helps to clean the message list.
     * @param messageList List<Message>, contains an object with a list of messages.
     * @return true when the list is cleaned or false when is not.
     */
    public boolean clearMessageList(List<Message> messageList){
        if(!messageList.isEmpty()){
            messageList.clear(); //clear list
            return true;
        }else{
            return false;
        }
    }


    /**
     * public void closeChat close the chat connection.
     */
    public void closeChat(){
        chat.closeConnection();
    }


    /**
     * private static String getRoom, gets the current room.
     * @return Sting room ID.
     */
    private static String getRoom() {
        return room;
    }


    /**
     * public List<Message> getChatMessages, gets the message list to be used for developers in the
     * chat view.
     * @return List<Message>, contains a list of messages from the Message object.
     */
    public List<Message> getChatMessages(){
        return Message.getMessageList();
    }
}
