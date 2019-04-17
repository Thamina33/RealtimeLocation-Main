package com.example.realtime_location.Model;

public class MyLocation {
    private int accuracy, altitude , bearing , bearingAccuracyDegree , speed , speedAccuracyMetersPerSeconds
            , verticalAccuracyMeters;
    private Boolean complete , fromMockProvider;
    private String provider;
    private long time , elapsedRealtimeNanos;
    private double latitude,longtitude;

    public MyLocation(){

    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }

    public int getBearingAccuracyDegree() {
        return bearingAccuracyDegree;
    }

    public void setBearingAccuracyDegree(int bearingAccuracyDegree) {
        this.bearingAccuracyDegree = bearingAccuracyDegree;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeedAccuracyMetersPerSeconds() {
        return speedAccuracyMetersPerSeconds;
    }

    public void setSpeedAccuracyMetersPerSeconds(int speedAccuracyMetersPerSeconds) {
        this.speedAccuracyMetersPerSeconds = speedAccuracyMetersPerSeconds;
    }

    public int getVerticalAccuracyMeters() {
        return verticalAccuracyMeters;
    }

    public void setVerticalAccuracyMeters(int verticalAccuracyMeters) {
        this.verticalAccuracyMeters = verticalAccuracyMeters;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Boolean getFromMockProvider() {
        return fromMockProvider;
    }

    public void setFromMockProvider(Boolean fromMockProvider) {
        this.fromMockProvider = fromMockProvider;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long elapsedRealtimeNanos) {
        this.elapsedRealtimeNanos = elapsedRealtimeNanos;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}