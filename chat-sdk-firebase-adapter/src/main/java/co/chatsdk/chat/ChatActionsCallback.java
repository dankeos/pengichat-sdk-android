package co.chatsdk.chat;

/**
 * ChatActionsCallback is a public interface that makes easy for developers to know about important
 * chat events.
 * when an agent is found, when the chat is closed and connection failed.
 *
 * @author <a href="mailto:danielduran@keos.co">Daniel Durán Schütz</a>
 * @version 1.0
 *
 */
public interface ChatActionsCallback {

    /**
     * Notify when the chat loads
     * @param welcomeMessage String, Welcome message from Pengi administrator
     * @param headMessage String, Head message managed from Pengi administrator
     */
    void onChatLoad(String welcomeMessage, String headMessage);

    /**
     * Notify when a message is received
     */
    void onMessageReceived();

    /**
     * Notify when an event onHold has been start
     *
     * @param msg String, may contains 2 values holdStatusOn or holdStatusOff
     * @param holdConfigMessage String, Hold message managed from Pengi administrator
     */
    void onHold(String msg, String holdConfigMessage);

    /**
     * Notify when a client is being transferred
     */
    void turnOnProgress();

    /**
     * Notify when a new user agent is found
     */
    void newUserAgentFound();

    /**
     * Notify when the chat connection is closed.
     * @param disconnectMessage String, this is the on close connection message managed from Pengi administrator.
     */
    void onCloseConnection(String disconnectMessage);

    /**
     * Notify when the connection is lost.
     * @param connectionFailMessage String, this is the on connection fail message managed from Pengi administrator.
     */
    void onConnectionFail(String connectionFailMessage);
}
