package co.chatsdk.firebase.generalfunc;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * UserAgentAvailableService is a public interface that request the getUserAgentAvaliable
 * endpoint and gets the userAgent data.
 *
 * @author <a href="mailto:danielduran@keos.co">Daniel Durán Schütz</a>
 * @version 1.0
 */
public interface UserAgentAvailableService {
    String API_ROUTE = "/generalFunctions/getUserAgentAvaliable";

    /**
     * getData sends parameters to getUserAgentAvaliable endpoint through GET and this response with
     * userAgent configuration data.
     *
     * @param channel String with the channel id, for chat is "2"
     * @param campaign int with the campaign id.
     * @return Call<JsonObject> with userAgent configuration.
     */
    @GET(API_ROUTE)
    Call<JsonObject> getData(@Query("channel") String channel, @Query("campaign") int campaign);
}
