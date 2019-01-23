package co.chatsdk.firebase.generalfunc;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import retrofit2.Response;

/**
 * The class takes the response of the call made by the interface PengiAuthorizationService
 * and process the params to produce part of the campaign configuration that will be used for
 * the initial chat configuration. This class receive the response asked by the PengiAuthorizationService
 * but no validates de the authentication (Validation is made by the endpoint).
 *
 * @author Daniel Durán Schütz. danielduran@keos.co
 * @version 1.0
 */
public class PengiAuthorizationConfig {

    public static final String TAG = "PengiAuthorizationConfig";

    //Campaign id
    private int campaign_id;

    //Json element with default messages
    private JsonElement messages_config;

    //Workdays Start at
    private String start;

    //Workdays End at
    private String end;

    //sunday is a workday?
    private int sunday;

    //monday is a workday?
    private int monday;

    //tuesday is a workday?
    private int tuesday;

    //wednesday is a workday?
    private int wednesday;

    //thursday is a workday?
    private int thursday;

    //friday is a workday?
    private int friday;

    //saturday is a workday?
    private int saturday;

    //Today string
    private String today;

    //Is available today?
    private boolean isAvailableToday;

    //Is in this time available
    private boolean isOnTimeAvailable;

    //Is open when isAvailableToday and isOnTimeAvailable are true
    public boolean isOpen;


    /**
     * Class constructor. Takes the object Response<JsonObject> authorizationData and extract some
     * data to contribute with the campaign configuration. Helps with the schedule and time validation,
     * produce the campaign ID and contribute with standard messages that are used in the
     * callbacks that we did for developers.
     *
     * @param authorizationData of type Response<JsonObject> contains important data for the campaign configuration.
     */
    PengiAuthorizationConfig(Response<JsonObject> authorizationData){
        JsonObject responseData = authorizationData.body().getAsJsonObject("msg");
        campaign_id = Integer.parseInt(String.valueOf(responseData.get("campaign_id")));
        sunday = Integer.parseInt(String.valueOf(responseData.get("sunday")));
        monday = Integer.parseInt(String.valueOf(responseData.get("monday")));
        tuesday = Integer.parseInt(String.valueOf(responseData.get("tuesday")));
        wednesday = Integer.parseInt(String.valueOf(responseData.get("wednesday")));
        thursday = Integer.parseInt(String.valueOf(responseData.get("thursday")));
        friday = Integer.parseInt(String.valueOf(responseData.get("friday")));
        saturday = Integer.parseInt(String.valueOf(responseData.get("saturday")));
        start = String.valueOf(responseData.get("start")).replace("\"", "");
        end = String.valueOf(responseData.get("end")).replace("\"", "");
        messages_config = responseData.get("messages_config");
        isOpen = isScheduleAvailable();
    }

    /**
     * Helps to know if the chat campaign is schedule and time available.
     * @return boolean to know if this business campaign are open or closed to receive clients.
     */
    private boolean isScheduleAvailable(){
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        Date currentTime = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String todayFormattedTime = dateFormat.format(currentTime);
        int currentHour = Integer.parseInt(todayFormattedTime.substring(0, 2));
        int currentMinute = Integer.parseInt(todayFormattedTime.substring(3, 5));
        int hourStart = Integer.parseInt(start.substring(0, 2));
        int minuteStart = Integer.parseInt(start.substring(3, 5));
        int hourEnd = Integer.parseInt(end.substring(0, 2));
        int minuteEnd = Integer.parseInt(end.substring(3, 5));
        boolean availableForToday = isAvailableToday(day);
        boolean availableForThisTime = isOnTimeAvailable(currentHour,currentMinute,hourStart,minuteStart,hourEnd,minuteEnd);

        return availableForToday && availableForThisTime;
    }

    /**
     * Helps to know if the bussines campaign is open today or not.
     * Takes today and validates with Pengi administrator if today the business will be open or not.
     *
     * @param day int, contains the day of the week extracted from Calendar.DAY_OF_WEEK
     * @return boolean, if today is open returns true else return false.
     */
    private boolean isAvailableToday(int day){
        switch (day){
            case Calendar.SUNDAY:
                today = "sunday";
                if (sunday == 1) isAvailableToday = true;
                else isAvailableToday = false;
                break;

            case Calendar.MONDAY:
                today = "monday";
                if(monday == 1) isAvailableToday = true;
                else isAvailableToday = false;
                break;

            case Calendar.TUESDAY:
                today = "tuesday";
                if(tuesday == 1) isAvailableToday = true;
                else isAvailableToday = false;
                break;

            case Calendar.WEDNESDAY:
                today = "wednesday";
                if(wednesday == 1) isAvailableToday = true;
                else isAvailableToday = false;
                break;

            case Calendar.THURSDAY:
                today = "thursday";
                if(thursday == 1) isAvailableToday = true;
                else isAvailableToday = false;
                break;

            case Calendar.FRIDAY:
                today = "friday";
                if(friday == 1) isAvailableToday = true;
                else isAvailableToday = false;
                break;

            case Calendar.SATURDAY:
                today = "saturday";
                if(saturday == 1) isAvailableToday = true;
                else isAvailableToday = false;
                break;
        }

        return isAvailableToday;
    }


    /**
     * Validates if now at this time the campaign of the business is open or not.
     * Takes the current time and validates when to start and end the workday, getting
     * the timer configuration from Pengi administrator.
     *
     * @param currentHour int with the current hour
     * @param currentMinute int with the current minute
     * @param hourStart int with the start hour retrieved from the the campaign configuration.
     * @param minuteStart int with the start minute retrieved from the the campaign configuration.
     * @param hourEnd int with the end hour retrieved from the the campaign configuration.
     * @param minuteEnd int with the end minute retrieved from the the campaign configuration.
     * @return boolean, true if is time available, false if not.
     */
    private boolean isOnTimeAvailable(int currentHour, int currentMinute, int hourStart, int minuteStart, int hourEnd, int minuteEnd){
        if(currentHour < hourStart){
            isOnTimeAvailable = false;
        }else if(currentHour > hourEnd){
            isOnTimeAvailable = false;
        }else{
            if(currentHour > hourStart && currentHour < hourEnd){
                isOnTimeAvailable = true;
            }else{
                if((currentHour == hourStart)){
                    if(currentMinute < minuteStart)isOnTimeAvailable = false;
                    else isOnTimeAvailable = true;
                }

                if((currentHour == hourEnd)){
                    if(currentMinute > minuteEnd)isOnTimeAvailable = false;
                    else isOnTimeAvailable = true;
                }
            }

        }

        return isOnTimeAvailable;
    }


    /**
     * Helps to extract the default messages from the campaign configuration.
     * @param type String with the reference of the message that will be retrieved.
     *             The value can be "bienvenida", "despedida", "desconexion", "hold",
     *             "encabezado" and "timer".
     *
     * @return an String with the wished message.
     */
    public String getMessages_config(String type){
        final JsonArray messages = messages_config.getAsJsonArray();
        int i;
        String messageWanted = "";

        for (i = 0 ; i < messages.size(); i++){
            String typeToCompare = String.valueOf(messages.get(i).getAsJsonObject().get("type")).replace("\"", "");
            if(type.equals(typeToCompare)){
                messageWanted = messages.get(i).getAsJsonObject().get("messages").toString();
            }
        }
        return messageWanted;
    }


    /**
     * getCampaign_id is a public getter for return the campaign ID.
     * @return int with the campaign id number.
     */
    public int getCampaign_id() {
        return campaign_id;
    }

}
