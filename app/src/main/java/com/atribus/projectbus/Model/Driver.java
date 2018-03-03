package com.atribus.projectbus.Model;

/**
 * Created by root on 3/3/18.
 */

public class Driver {
    private String name;
    private String phonenumber;
    private String uuid;
    private String dateOfBirth;
    private String license;
    private String busroute;
    private Double latitude,longitude;

    public Driver(String name, String phonenumber, String uuid, String dateOfBirth, String license, String busroute, Double latitude, Double longitude) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.uuid = uuid;
        this.dateOfBirth = dateOfBirth;
        this.license = license;
        this.busroute = busroute;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Driver() {
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

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
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
