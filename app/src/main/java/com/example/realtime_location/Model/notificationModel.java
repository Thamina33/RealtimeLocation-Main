package com.example.realtime_location.Model;

public class notificationModel {
    String nuid , uid , email  ;

    public notificationModel() {
    }


    public notificationModel(String nuid, String uid, String email) {
        this.nuid = nuid;
        this.uid = uid;
        this.email = email;
    }

    public String getNuid() {
        return nuid;
    }

    public void setNuid(String nuid) {
        this.nuid = nuid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
