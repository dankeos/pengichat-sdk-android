package co.chatsdk.firebase.generalfunc;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * UpdateTokenChatConsultantService is a public interface that request the updateTokenChatConsultant
 * endpoint and makes an update in database about the token of the consultant.
 *
 * @author <a href="mailto:danielduran@keos.co">Daniel Durán Schütz</a>
 * @version 1.0
 */
public interface UpdateTokenChatConsultantService {
    String API_ROUTE = "/generalFunctions/updateTokenChatConsultant";

    /**
     * getData sends parameters to updateTokenChatConsultant endpoint through POST
     *
     * @param id int with the consultant id.
     * @param tc String with the room id.
     * @return Call<JsonObject> with the request result.
     */
    @POST(API_ROUTE)
    @FormUrlEncoded
    Call<JsonObject> getData(@Field("id") int id, @Field("tc") String tc);
}
