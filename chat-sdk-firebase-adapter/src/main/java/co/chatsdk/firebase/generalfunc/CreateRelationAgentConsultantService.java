package co.chatsdk.firebase.generalfunc;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * CreateRelationAgentConsultantService is a public interface that request the
 * createRelationAgentConsultant endpoint.
 *
 * @author <a href="mailto:danielduran@keos.co">Daniel Durán Schütz</a>
 * @version 1.0
 */
public interface CreateRelationAgentConsultantService {
    String API_ROUTE = "/generalFunctions/createRelationAgentConsultant";

    /**
     * getData in this context sends parameters to createRelationAgentConsultant endpoint through POST.
     *
     * @param idConsultant int with the consultant ID.
     * @param idAgent int value with the agent ID.
     * @return a Call<JsonObject> with the request result.
     */
    @POST(API_ROUTE)
    @FormUrlEncoded
    Call<JsonObject> getData(@Field("idConsultant") int idConsultant, @Field("idAgent") int idAgent);
}
