package com.example.realtime_location.Utils;

import com.example.realtime_location.Model.User;
import com.example.realtime_location.Remote.RetrofitClient;
import com.example.realtime_location.Remote.lFCMService;

import retrofit2.Retrofit;

public class Common {


    public static final String USER_INFORMATION = "UserInformation";
    public static final String USER_UID_SAVE_KEY = "SaveUid";
    public static final String TOKENS ="Tokens" ;
    public static final String FROM_NAME ="FromName" ;
    public static final String ACCEPT_LIST = "acceptList";
    public static final String FROM_UID = "FromUid";
    public static final String To_UID ="ToUid" ;
    public static final String To_NAME = "ToName";
    public static final String FRIEND_REQUEST = "FriendRequests";
    public static final String Public_Location ="PublicLocation" ;
    public static User loggedUser;

    public static lFCMService getFCMSerice(){
        return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(lFCMService.class);

    }

}
