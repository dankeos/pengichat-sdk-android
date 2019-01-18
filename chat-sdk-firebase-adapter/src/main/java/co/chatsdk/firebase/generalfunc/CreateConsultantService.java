package co.chatsdk.firebase.generalfunc;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * CreateConsultantService is a public interface that request the createConsultant endpoint for
 * create a consultant user.
 *
 * @author <a href="mailto:danielduran@keos.co">Daniel Durán Schütz</a>
 * @version 1.0
 *
 */
public interface CreateConsultantService {
    String API_ROUTE = "/generalFunctions/createConsultant";

    /**
     * getData send parameters to createConsultant endpoint through POST.
     *
     * @param name String, the consultant name.
     * @param email String, the consultant email.
     * @param ubication String, the consultant city and country, etc.
     * @param others_params String, if sdk is used: just an empty array "[]"
     * @param openfire_username String, if sdk is used: empty value ""
     * @param openfire_room String, if sdk is used:  empty value ""
     * @param originToken String, the sdk android token provided by Keos from Pengi administrator
     * @param openfireIdentifier String, most to have "pw_co" value.
     * @param token_peer String, if sdk is used: most to have "null" value.
     * @param token_internal String, the agent token
     * @param token_chat String, The room of the chat
     * @return Call<JsonObject> that must have the consultant ID.
     */
    @POST(API_ROUTE)
    @FormUrlEncoded
    Call<JsonObject> getData(@Field("name") String name,
                             @Field("email") String email,
                             @Field("ubication") String ubication,
                             @Field("others_params") String others_params,
                             @Field("openfire_username") String openfire_username,
                             @Field("openfire_room") String openfire_room,
                             @Field("originToken") String originToken,
                             @Field("openfireIdentifier") String openfireIdentifier,
                             @Field("token_peer") String token_peer,
                             @Field("token_internal") String token_internal,
                             @Field("token_chat") String token_chat);
}
