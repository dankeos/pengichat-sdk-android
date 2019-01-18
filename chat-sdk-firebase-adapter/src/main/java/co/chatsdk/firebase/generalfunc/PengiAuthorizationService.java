package co.chatsdk.firebase.generalfunc;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * PengiAuthorizationService is a public interface that request the consultPengiSdkAuthorization
 * endpoint that authenticates and gets all campaign configuration.
 *
 * @author <a href="mailto:danielduran@keos.co">Daniel Durán Schütz</a>
 * @version 1.0
 */
public interface PengiAuthorizationService{
    String API_ROUTE = "/generalFunctions/consultPengiSdkAuthorization";

    /**
     * getData sends parameters to PengiAuthorizationService endpoint through POST and gets the
     * campaign configuration object.
     *
     * @param authToken String, the sdk token provided by Keos from Pengi administrator
     * @param originApp String that contains the unique app id, example "co.keos.myapplication".
     * @return The Call<JsonObject> with the campaign configuration
     */
    @POST(API_ROUTE)
    @FormUrlEncoded
    Call<JsonObject> getData(@Field("authToken") String authToken,
                             @Field("app_id") String originApp);
}
