package com.atribus.projectbus.Model;

/**
 * Created by root on 1/3/18.
 */

public class User {
    private String name;
    private String phonenumber;
    private String uuid;
    private String dateOfBirth;
    private String areaname;
    private String pickuppoint;
    private String gender;
    private String busroute;
    private Double latitude,longitude;

    public User(String name, String phonenumber, String uuid, String dateOfBirth, String areaname, String pickuppoint,
                String gender, String busroute, Double latitude, Double longitude) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.uuid = uuid;
        this.dateOfBirth = dateOfBirth;
        this.areaname = areaname;
        this.pickuppoint = pickuppoint;
        this.gender = gender;
        this.busroute = busroute;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public User() {
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getPickuppoint() {
        return pickuppoint;
    }

    public void setPickuppoint(String pickuppoint) {
        this.pickuppoint = pickuppoint;
    }

    public String getBusroute() {
        return busroute;
    }

    public void setBusroute(String busroute) {
        this.busroute = busroute;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
