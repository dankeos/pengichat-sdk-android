package co.chatsdk.firebase.generalfunc;

import com.google.gson.JsonObject;
import retrofit2.Response;

/**
 * The class takes the response of the call made by the interface CreateConsultantService
 * and process the params to produce the id Consultant.
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 */
public class CreateConsultantConfig {
    private static final String TAG = "CreateConsultantConfig";
    private int id;

    /**
     * Class constructor. Process the consultantResponse of type Response<JsonObject> and
     * produce the consultant ID.
     *
     * @param consultantResponse of type Response<JsonObject> contains the response of endpoint
     *                           CreateConsultantService and extract the consultant Id.
     */
    CreateConsultantConfig(Response<JsonObject> consultantResponse){
        JsonObject responseData = consultantResponse.body().getAsJsonObject("msg");
        id = Integer.parseInt(String.valueOf(responseData.get("id")));
    }

    /**
     * getId is a public getter for return the consultant ID.
     * @return an int with the consultant ID.
     */
    public int getId() {
        return id;
    }
}
