package co.chatsdk.firebase.generalfunc;

import com.google.gson.JsonObject;
import retrofit2.Response;

/**
 * This class helps to extract some important fields from the response of endpoint getUserAgentAvaliable.
 * Extract token_internal that is the agent token, the id of the agent and the room id. Contribute with
 * another classes with his getters and setters.
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 */
public class UserAgentAvailableConfig {
    public static final String TAG = "UserAgentAvailable";
    private int id;
    private String random_room;
    private String token_agent;


    /**
     * Class constructor. Extracts the value from response and produces random_room (room),
     * token_internal (agent token) and id (agent id)
     *
     * @param userAgentData of type Response<JsonObject> contains the response with the userAgent
     *                      configuration.
     */
    UserAgentAvailableConfig(Response<JsonObject> userAgentData){
        JsonObject responseData = userAgentData.body().getAsJsonObject("msg");
        random_room = String.valueOf(responseData.get("random_room")).replace("\"", "");
        token_agent = String.valueOf(responseData.get("token_internal")).replace("\"", "");
        id = Integer.parseInt(String.valueOf(responseData.get("id")));
    }


    /**
     * Public getter for return the token of the agent.
     * @return String with the token agent
     */
    public String getToken_agent() {
        return token_agent;
    }


    /**
     * Public getter for return the room ID.
     * @return String with the room ID.
     */
    public String getRandom_room() {
        return random_room;
    }


    /**
     * Public getter for return the agent id.
     * @return int with the id of the agent.
     */
    public int getId() {
        return id;
    }


    /**
     * Public setter that sets the agent id.
     * @param id int with the agent id.
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Public setter that sets the room.
     * @param random_room String with the room id.
     */
    public void setRandom_room(String random_room) {
        this.random_room = random_room;
    }


    /**
     * Public setter that sets the token_agent
     * @param token_agentFrom String with the agent token
     */
    public void setToken_agent(String token_agentFrom) {
        this.token_agent = token_agentFrom;
    }

}
