package co.chatsdk.core.session;

import android.content.Context;
import android.graphics.Color;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import co.chatsdk.core.interfaces.CrashHandler;
import co.chatsdk.core.utils.StringChecker;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class helps to create and build the chatSdk general configuration.
 * Created by ben on 10/17/17.
 */

public class Configuration {
    public static final String TAG = "Configuration";

    public WeakReference<Context> context;

    //Userdata params
    public String nameUser = "anonymous";
    public String userEmail = "";
    public String location = "";

    //Pengi authentication data
    public String authToken;
    public String originApp;


    //Services connection
    public static String servicesUrl = "https://pg02.pengi.co:3001";
    public static Retrofit retrofit;

    //Time to kill the agent chat when the app is killed in seconds
    public int timeToKillChat = 60;


    // Basic parameters
    public int messagesToLoadPerBatch = 30;
    public int contactsToLoadPerBatch = 20;

    // Testing
    public boolean debug = true;
    public String debugUsername = null;
    public String debugPassword = null;
    public CrashHandler crashHandler;

    // Twitter Login
    public String twitterKey;
    public String twitterSecret;

    // Google
    public String googleMapsApiKey;
    public String googleWebClientKey;

    // Firebase
    public String firebaseRootPath = "pengi/live";
    public String firebaseStorageUrl;
    public String firebaseCloudMessagingServerKey;

    // Should we call disconnect when the app is in the background for more than 5 seconds?
    public boolean disconnectFromFirebaseWhenInBackground = true;

    // XMPP
    public String xmppDomain;
    public String xmppHostAddress;
    public int xmppPort;
    public String xmppResource = "Android";
    public boolean xmppSslEnabled;
    public boolean xmppAcceptAllCertificates;
    public boolean xmppDisableHostNameVerification;
    public boolean xmppAllowClientSideAuthentication;
    public boolean xmppCompressionEnabled;
    public String xmppSecurityMode = "disabled";
    public int xmppMucMessageHistory = 20;

    // Push notification
    public int pushNotificationImageDefaultResourceId;
    public String pushNotificationAction;
    public boolean unreadMessagesCountForPublicChatRoomsEnabled;
    public boolean inboundPushHandlingEnabled = true;

    // Should the client send the push or is a server script handling it?
    public boolean clientPushEnabled = false;

    // If this is true, then we will only send a push notification if the recipient is offline
    public boolean onlySendPushToOfflineUsers = false;
    public boolean showEmptyChats = false;

    // Contact Book
    public String contactBookInviteContactEmailSubject;
    public String contactBookInviteContactEmailBody;
    public String contactBookInviteContactSmsBody;

    // Login
    public boolean autoLoginEnabled = true;
    public boolean anonymousLoginEnabled = true;
    public boolean facebookLoginEnabled = false;
    public boolean twitterLoginEnabled = false;
    public boolean googleLoginEnabled = false;

    public boolean resetPasswordEnabled = false;

    // Message types
    public boolean imageMessagesEnabled = true;
    public boolean locationMessagesEnabled = true;

    // Chat options
    public boolean groupsEnabled = false;
    public boolean threadDetailsEnabled = true;
    public boolean publicRoomCreationEnabled = false;
    public boolean saveImagesToDirectory = false;

    public int messageColorMe = Color.parseColor("#b0cfea");
    public int messageColorReply = Color.parseColor("#dadada");

    public int messageTextColorMe = Color.parseColor("#222222");
    public int messageTextColorReply = Color.parseColor("#222222");

    public String messageTimeFormat = "HH:mm";
    public String lastOnlineTimeFormat = "HH:mm";

    public int maxMessagesToLoad = 30;
    public int imageMaxWidth = 1920;
    public int imageMaxHeight = 2560;
    public int imageMaxThumbnailDimension = 400;
    public int maxInboxNotificationLines = 7;
    public boolean imageCroppingEnabled = true;

    public String defaultNamePrefix = "ChatSDK";
    public String defaultName = null;

    public String imageDirectoryName = "ChatSDK";
    public String contactDeveloperEmailAddress = "danielduran@keos.co";
    public String contactDeveloperEmailSubject = "";
    public String contactDeveloperDialogTitle = "";
    public String defaultUserAvatarURL = "http://flathash.com/" + String.valueOf(new Random().nextInt(1000)) + ".png";
    public int audioMessageMaxLengthSeconds = 300;

    public String pushNotificationSound = "";
    public boolean showLocalNotifications = true;
    public int pushNotificationColor = Color.parseColor("#ff33b5e5");
    public boolean pushNotificationsForPublicChatRoomsEnabled;

    // If this is set to true, we will simulate what happens when a push is recieved and the app
    // is in the killed state. This is useful to help us debug that process.
    public boolean backgroundPushTestModeEnabled = false;

    public int loginScreenDrawableResourceID = -1;

    public long readReceiptMaxAge = TimeUnit.DAYS.toMillis(7);

    public HashMap<String, Object> customProperties = new HashMap<>();

    public Object getCustomProperty (String key) {
        return customProperties.get(key);
    }

    public void updateDefaultName () {
        defaultName = defaultNamePrefix + String.valueOf(new Random().nextInt(1000));
    }

    public boolean twitterLoginEnabled () {
        return !StringChecker.isNullOrEmpty(twitterKey) && !StringChecker.isNullOrEmpty(twitterSecret) && twitterLoginEnabled;
    }

    public boolean googleLoginEnabled () {
        return !StringChecker.isNullOrEmpty(googleWebClientKey) && googleLoginEnabled;
    }

    public boolean facebookLoginEnabled () {
        return facebookLoginEnabled;
    }

    public static class Builder {

        private Configuration  config;

        public Builder (Context context) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(servicesUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            config = new Configuration();
            config.context = new WeakReference<>(context);
        }

        /**
         * Sets the time in seconds to kill session
         * @param time int
         * @return
         */
        public Builder setTimeToKillChat(int time){
            config.timeToKillChat = time;
            return this;
        }


        public Builder debugModeEnabled (boolean debug) {
            config.debug = debug;
            return this;
        }

        public Builder debugUsername (String username) {
            config.debugUsername = username;
            return this;
        }

        public Builder debugPassword (String password) {
            config.debugPassword = password;
            return this;
        }

        public Builder twitterLogin (String key, String secret) {
            config.twitterKey = key;
            config.twitterSecret = secret;
            return this;
        }

        public Builder googleLogin (String webClientKey) {
            config.googleWebClientKey = webClientKey;
            return this;
        }

        public Builder googleMaps (String mapsApiKey) {
            config.googleMapsApiKey = mapsApiKey;
            return this;
        }

        public Builder imageCroppingEnabled (boolean enabled) {
            config.imageCroppingEnabled = enabled;
            return this;
        }

        public Builder firebaseCloudMessagingServerKey (String cloudMessagingServerKey) {
            config.firebaseCloudMessagingServerKey = cloudMessagingServerKey;
            return this;
        }

        public Builder firebase (String rootPath, String cloudMessagingServerKey) {
            if(rootPath != null && rootPath.length() > 0 && !rootPath.substring(rootPath.length() - 1).equals('/')) {
                rootPath += "/";
            }

            firebaseRootPath(rootPath);
            firebaseCloudMessagingServerKey(cloudMessagingServerKey);

            return this;
        }

        public Builder firebaseStorageURL (String firebaseStorage) {
            config.firebaseStorageUrl = firebaseStorage;
            return this;
        }

        public Builder firebaseRootPath (String rootPath) {
            config.firebaseRootPath = rootPath;
            return this;
        }

        /**
         * In this case the resource will be set to the device's IMEI number
         * @param domain
         * @param hostAddress
         * @param port
         * @return
         */
        public Builder xmpp (String domain, String hostAddress, int port) {
            return xmpp(domain, hostAddress, port, null);
        }

        public Builder xmpp (String domain, String hostAddress, int port, String resource) {
            return xmpp(domain, hostAddress, port, resource, false);
        }

        public Builder xmpp (String domain, String hostAddress, int port, String resource, boolean sslEnabled) {
            config.xmppDomain = domain;
            config.xmppHostAddress = hostAddress;
            config.xmppPort = port;
            config.xmppResource = resource;
            config.xmppSslEnabled = sslEnabled;
            return this;
        }

        public Builder setAudioMessageMaxLengthSeconds (int seconds) {
            config.audioMessageMaxLengthSeconds = seconds;
            return this;
        }

        public Builder setXxmppAcceptAllCertificates (boolean acceptAllCertificates) {
            config.xmppAcceptAllCertificates = acceptAllCertificates;
            return this;
        }

        public Builder resetPasswordEnabled (boolean resetPasswordEnabled) {
            config.resetPasswordEnabled = resetPasswordEnabled;
            return this;
        }

        public Builder setXmppSslEnabled (boolean sslEnabled) {
            config.xmppSslEnabled = sslEnabled;
            return this;
        }

        public Builder setXmppMucMessageHistory (int history) {
            config.xmppMucMessageHistory = history;
            return this;
        }

        public Builder setXmppDisableHostNameVerification (boolean disableHostNameVerification) {
            config.xmppDisableHostNameVerification = disableHostNameVerification;
            return this;
        }

        /**
         * This setting is not currently implemented
         * @param allowClientSideAuthentication
         * @return
         */
        public Builder setXmppAllowClientSideAuthentication (boolean allowClientSideAuthentication) {
            config.xmppAllowClientSideAuthentication = allowClientSideAuthentication;
            return this;
        }

        public Builder setXmppCompressionEnabled (boolean compressionEnabled) {
            config.xmppCompressionEnabled = compressionEnabled;
            return this;
        }

        /**
         * Set TSL security mode. Allowable values are
         * "required"
         * "ifpossible"
         * "disabled"
         * @param securityMode
         * @return
         */
        public Builder setXmppSecurityMode (String securityMode) {
            config.xmppSecurityMode = securityMode;
            return this;
        }

        public Builder contactBook (String inviteEmailSubject, String inviteEmailBody, String inviteSmsBody) {
            config.contactBookInviteContactEmailSubject = inviteEmailSubject;
            config.contactBookInviteContactEmailBody = inviteEmailBody;
            config.contactBookInviteContactSmsBody = inviteSmsBody;
            return this;
        }

        public Builder disconnectFromFirebaseWhenInBackground (boolean disconnect) {
            config.disconnectFromFirebaseWhenInBackground = disconnect;
            return this;
        }

         public Builder publicRoomCreationEnabled (boolean value) {
            config.publicRoomCreationEnabled = value;
            return this;
        }

        public Builder autoLoginEnabled (boolean value) {
            config.autoLoginEnabled = value;
            return this;
        }
        public Builder anonymousLoginEnabled (boolean value) {
            config.anonymousLoginEnabled = value;
            return this;
        }

        public Builder facebookLoginEnabled (boolean value) {
            config.facebookLoginEnabled = value;
            return this;
        }

        public Builder setPushNotificationAction (String action) {
            config.pushNotificationAction = action;
            return this;
        }

        public Builder setShowEmptyChats (boolean showEmpty) {
            config.showEmptyChats = showEmpty;
            return this;
        }

        public Builder setInboundPushHandlingEnabled (boolean enabled) {
            config.inboundPushHandlingEnabled = enabled;
            return this;
        }

        public Builder pushNotificationsForPublicChatRoomsEnabled(boolean value) {
            config.pushNotificationsForPublicChatRoomsEnabled = value;
            return this;
        }

        public Builder unreadMessagesCountForPublicChatRoomsEnabled(boolean value) {
            config.unreadMessagesCountForPublicChatRoomsEnabled = value;
            return this;
        }

        public Builder twitterLoginEnabled (boolean value) {
            config.twitterLoginEnabled = value;
            return this;
        }

        public Builder googleLoginEnabled (boolean value) {
            config.googleLoginEnabled = value;
            return this;
        }

        public Builder imageMessagesEnabled (boolean value) {
            config.imageMessagesEnabled = value;
            return this;
        }

        public Builder locationMessagesEnabled (boolean value) {
            config.locationMessagesEnabled = value;
            return this;
        }

        public Builder groupsEnabled (boolean value) {
            config.groupsEnabled = value;
            return this;
        }

        public Builder contactsToLoadPerBatch (int number) {
            config.contactsToLoadPerBatch = number;
            return this;
        }

        public Builder messagesToLoadPerBatch (int number) {
            config.messagesToLoadPerBatch = number;
            return this;
        }

        public Builder setMessageColorMe (int color) {
            config.messageColorMe = color;
            return this;
        }

        public Builder setMessageColorReply (int color) {
            config.messageColorReply = color;
            return this;
        }

        public Builder setMessageColorMe (String hexColor) {
            config.messageColorMe = Color.parseColor(hexColor);
            return this;
        }

        public Builder setMessageColorReply (String hexColor) {
            config.messageColorReply = Color.parseColor(hexColor);
            return this;
        }

        public Builder setBackgroundPushTestModeEnabled (boolean enabled) {
            config.backgroundPushTestModeEnabled = enabled;
            return this;
        }

        public Builder setMessageTextColorMe (int color) {
            config.messageTextColorMe = color;
            return this;
        }

        public Builder setMessageTextColorReply (int color) {
            config.messageTextColorReply = color;
            return this;
        }

        public Builder setMessageTextColorMe (String hexColor) {
            config.messageTextColorMe = Color.parseColor(hexColor);
            return this;
        }

        public Builder setMessageTextColorReply (String hexColor) {
            config.messageTextColorReply = Color.parseColor(hexColor);
            return this;
        }

        public Builder setCrashHandler (CrashHandler handler) {
            config.crashHandler = handler;
            return this;
        }

        public Builder setClientPushEnabled (boolean clientPushEnabled) {
            config.clientPushEnabled = clientPushEnabled;
            return this;
        }

        public Builder setShowLocalNotifications (boolean show) {
            config.showLocalNotifications = show;
            return this;
        }

        public Builder threadDetailsEnabled (boolean value) {
            config.threadDetailsEnabled = value;
            return this;
        }

        public Builder saveImagesToDirectoryEnabled (boolean value) {
            config.saveImagesToDirectory = value;
            return this;
        }

        public Builder maxMessagesToLoad (int value) {
            config.maxMessagesToLoad = value;
            return this;
        }

        public Builder maxImageWidth (int value) {
            config.imageMaxWidth = value;
            return this;
        }

        public Builder maxImageHeight (int value) {
            config.imageMaxHeight = value;
            return this;
        }

        public Builder maxThumbnailDimensions (int value) {
            config.imageMaxThumbnailDimension = value;
            return this;
        }

        public Builder maxInboxNotificationLines (int value) {
            config.maxInboxNotificationLines = value;
            return this;
        }

        public Builder defaultNamePrefix(String value) {
            config.defaultNamePrefix = value;
            config.updateDefaultName();
            return this;
        }

        public Builder defaultName(String value) {
            config.defaultName = value;
            return this;
        }

        public Builder setMessageTimeFormat (String format) {
            config.messageTimeFormat = format;
            return this;
        }

        public Builder setLastOnlineTimeFormat (String format) {
            config.lastOnlineTimeFormat = format;
            return this;
        }

        public Builder loginScreenDrawableResourceID (int resource) {
            config.loginScreenDrawableResourceID = resource;
            return this;
        }

        public Builder contactDeveloperEmailAddress (String value) {
            config.contactDeveloperEmailAddress = value;
            return this;
        }

        public Builder contactDeveloperEmailSubject (String value) {
            config.contactDeveloperEmailSubject = value;
            return this;
        }

        public Builder contactDeveloperDialogTitle (String value) {
            config.contactDeveloperDialogTitle = value;
            return this;
        }

        public Builder defaultUserAvatarUrl (String value) {
            config.defaultUserAvatarURL = value;
            return this;
        }

        public Builder imageDirectoryName (String value) {
            config.imageDirectoryName = value;
            return this;
        }

        public Builder readReceiptMaxAge (long millis) {
            config.readReceiptMaxAge = millis;
            return this;
        }

        public Builder addCustomSetting (String key, Object value) {
            config.customProperties.put(key, value);
            return this;
        }

        public Builder pushNotificationImageDefaultResourceId (int resourceId) {
            config.pushNotificationImageDefaultResourceId = resourceId;
            return this;
        }

        public Builder onlySendPushToOfflineUsers (boolean value) {
            config.onlySendPushToOfflineUsers = value;
            return this;
        }

        public Builder pushNotificationSound (String sound) {
            config.pushNotificationSound = sound;
            return this;
        }

        public Builder pushNotificationColor (String hexColor) {
            config.pushNotificationColor = Color.parseColor(hexColor);
            return this;
        }

        public Builder pushNotificationColor (int color) {
            config.pushNotificationColor = color;
            return this;
        }

        /**
         * Sets the name of the client
         * @param name
         * @return
         */
        public Builder setNameUser(String name){
            config.nameUser = name;
            return this;
        }

        /**
         * Sets the user client email
         * @param usermail
         * @return
         */
        public Builder setUserEmail(String usermail){
            config.userEmail = usermail;
            return this;
        }

        /**
         * Sets the client location
         * @param locationNow
         * @return
         */
        public Builder setLocation(String locationNow){
            config.location = locationNow;
            return this;
        }

        /**
         * Sets the authentication token
         * @param token
         * @return
         */
        public Builder setAuthToken(String token){
            config.authToken = token;
            return this;
        }

        /**
         * Sets the App Id.
         * @param appId
         * @return
         */
        public Builder setOriginApp(String appId){
            config.originApp = appId;
            return this;
        }

        public Configuration build () {
            return config;
        }

    }
}
