package com.example.realtime_location.Remote;

import android.app.DownloadManager;

import com.example.realtime_location.Model.MyResponse;
import com.example.realtime_location.Model.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface lFCMService {

    @Headers({
                "Content-Type:application/json",
                   "Authorization:key=AAAAg8deFpo:APA91bFP8DRHS6qb7MqxkzPB2ymVQDSmW5aUH8M3BAPI4AciiDuh-UHTMxAS1y76HiISWW-PRU1DQBTk06dvgHIwtc9CtH93cGASHt5H5THNA68tBxpDLgrN0wjaPBcslo_6P2cpf4lm"
    })
    @POST("fcm/send")
    Observable<MyResponse> sendFriendRequestToUser(@Body Request body);
}

