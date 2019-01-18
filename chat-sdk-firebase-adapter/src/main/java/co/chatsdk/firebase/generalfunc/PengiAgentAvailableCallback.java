package co.chatsdk.firebase.generalfunc;

/**
 * PengiAgentAvailableCallback is a public interface that notify for developers about important
 * events when an agent is requested.
 *
 * @author <a href="mailto:danielduran@keos.co">Daniel Durán Schütz</a>
 * @version 1.0
 *
 */
public interface PengiAgentAvailableCallback {

    /**
     * Notify when an agent is being searched
     */
    void onSearchAgent();


    /**
     * Notify when an agent is available
     */
    void onAgentAvailable();


    /**
     * Notify when there are no agents available
     */
    void noAgentsAvailable();


    /**
     * Notify when the call to the endpoint fail or something like that
     */
    void onCallFailure();


    /**
     * Notify if the chat is available in the schedule and calendar from Pengi administrator
     * @param messageSchedule String Not available on time message from Pengi administrator
     */
    void onUnavailableSchedule(String messageSchedule);
}
