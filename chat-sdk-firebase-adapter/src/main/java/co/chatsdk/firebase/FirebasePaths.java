/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:34 PM
 */

package co.chatsdk.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.session.ChatSDK;

public class FirebasePaths{

    public static final String TAG ="FirebasePaths";

    public static final String UsersPath = "sockets";
    public static final String MessagesPath = "chat";
    public static final String ThreadsPath = "rooms";
    public static final String PublicThreadsPath = "public-threads";
    public static final String DetailsPath = "details";
    public static final String IndexPath = "searchIndex";
    public static final String OnlinePath = "online";
    public static final String MetaPath = "meta";
    public static final String FollowersPath = "followers";
    public static final String FollowingPath = "follows";
    public static final String Image = "imaeg";
    public static final String Thumbnail = "thumbnail";
    public static final String UpdatedPath = "updated";
    public static final String LastMessagePath = "lastMessage";
    public static final String TypingPath = "typing";
    public static final String ReadPath = Keys.Read;
    public static final String LocationPath = "location";


    /* Not sure if this the wanted implementation but its give the same result as the objective-C code.*/
    /** @return The main databse ref.*/

    public static DatabaseReference firebaseRawRef() {
        Log.e(TAG, "firebaseRawRef");
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference firebaseRef () {
        Log.e(TAG, "firebaseRef");
        return firebaseRawRef().child(ChatSDK.config().firebaseRootPath);
    }

    /* Users */
    /** @return The users main ref.*/
    public static DatabaseReference usersRef(){
        Log.e(TAG, "usersRef");
        return firebaseRef().child(UsersPath);
    }

    /** @return The user ref for given id.*/
    public static DatabaseReference userRef(String firebaseId){
        Log.e(TAG, "userRef");
        return usersRef().child(firebaseId);
    }

    /** @return The user threads ref.*/
    public static DatabaseReference userThreadsRef(String firebaseId){
        Log.e(TAG, "userThreadsRef");
        return usersRef().child(firebaseId).child(ThreadsPath);
    }

    /** @return The user meta ref for given id.*/
    public static DatabaseReference userMetaRef(String firebaseId){
        Log.e(TAG, "userMetaRef");
        return usersRef().child(firebaseId).child(MetaPath);
    }

    public static DatabaseReference userOnlineRef(String firebaseId){
        Log.e(TAG, "userOnlineRef");
        return userRef(firebaseId).child(OnlinePath);
    }



    public static DatabaseReference userFollowingRef(String firebaseId){
        Log.e(TAG, "userFollowingRef");
        return userRef(firebaseId).child(FollowingPath);
    }

    public static DatabaseReference userFollowersRef(String firebaseId){
        Log.e(TAG, "userFollowersRef");
        return userRef(firebaseId).child(FollowersPath);
    }

    /* Threads */
    /** @return The thread main ref.*/
    public static DatabaseReference threadRef(){
        Log.e(TAG, "threadRef");
        return firebaseRef().child(ThreadsPath);
    }

    /** @return The thread ref for given id.*/
    public static DatabaseReference threadRef(String firebaseId){
        Log.e(TAG, "threadRef with params");
        return threadRef().child(firebaseId);
    }

    public static DatabaseReference threadUsersRef(String firebaseId){
        Log.e(TAG, "threadUsersRef");
        return threadRef().child(firebaseId).child(UsersPath);
    }

    public static DatabaseReference threadDetailsRef(String firebaseId){
        Log.e(TAG, "threadDetailsRef");
        return threadRef().child(firebaseId).child(DetailsPath);
    }

    public static DatabaseReference threadLastMessageRef(String firebaseId){
        Log.e(TAG, "threadLastMessageRef");
        return threadRef().child(firebaseId).child(LastMessagePath);
    }

    public static DatabaseReference threadMessagesRef(String firebaseId){
            Log.e(TAG, "threadMessagesRef");
        return threadRef(firebaseId).child(MessagesPath);
    }

    public static DatabaseReference threadMetaRef(String firebaseId){
        Log.e(TAG, "threadMetaRef");
        return threadRef(firebaseId).child(MetaPath);
    }

    public static DatabaseReference publicThreadsRef(){
        Log.e(TAG, "publicThreadsRef");
        return firebaseRef().child(PublicThreadsPath);
    }

    public static DatabaseReference onlineRef(String userEntityID){
        Log.e(TAG, "onlineRef");
        return firebaseRef().child(OnlinePath).child(userEntityID);
    }


    /* Index */
    public static DatabaseReference indexRef(){
        Log.e(TAG, "indexRef");
        return firebaseRef().child(IndexPath);
    }

}
