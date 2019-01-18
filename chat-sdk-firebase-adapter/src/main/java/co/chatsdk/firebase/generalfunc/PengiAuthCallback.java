package co.chatsdk.firebase.generalfunc;

/**
 * PengiAuthCallback is a public interface that notify for developers about the needed Pengi chatSDK authentication
 * process.
 *
 * @author <a href="mailto:danielduran@keos.co">Daniel Durán Schütz</a>
 * @version 1.0
 *
 */
public interface PengiAuthCallback {

    /**
     * Notify when all is right in authentication process
     */
    void onAuthenticationSuccess();


    /**
     * Notify when the authentication was not correct, maybe for token or for another
     * reason like the status can not be read.
     */
    void onAuthenticationFail();


    /**
     * Notify when the endpoint request fail and is different to 200 code
     */
    void onCallFailure();
}
