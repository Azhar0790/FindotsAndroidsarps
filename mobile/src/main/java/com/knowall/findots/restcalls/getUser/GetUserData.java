package com.knowall.findots.restcalls.getUser;

import org.parceler.Parcel;

/**
 * Created by parijathar on 8/9/2016.
 */
@Parcel
public class GetUserData {
    private boolean isLogin;

    private String checkedInDate;

    private String checkedOutDate;

    private String destinationName;

    private boolean checkedIN;

    private boolean isDeleted;

    private String lastLoggedInDate;

    private boolean isPermissions;

    private int userID;

    private String address;

    private String name;

    private double longitude;

    private double latitude;

    private boolean checkedOut;

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getCheckedInDate() {
        return checkedInDate;
    }

    public void setCheckedInDate(String checkedInDate) {
        this.checkedInDate = checkedInDate;
    }

    public String getCheckedOutDate() {
        return checkedOutDate;
    }

    public void setCheckedOutDate(String checkedOutDate) {
        this.checkedOutDate = checkedOutDate;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public boolean isCheckedIN() {
        return checkedIN;
    }

    public void setCheckedIN(boolean checkedIN) {
        this.checkedIN = checkedIN;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getLastLoggedInDate() {
        return lastLoggedInDate;
    }

    public void setLastLoggedInDate(String lastLoggedInDate) {
        this.lastLoggedInDate = lastLoggedInDate;
    }

    public boolean isPermissions() {
        return isPermissions;
    }

    public void setPermissions(boolean permissions) {
        isPermissions = permissions;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [isLogin = "+isLogin+", checkedInDate = "+checkedInDate+", checkedOutDate = "+checkedOutDate+", destinationName = "+destinationName+", checkedIN = "+checkedIN+", isDeleted = "+isDeleted+", lastLoggedInDate = "+lastLoggedInDate+", isPermissions = "+isPermissions+", userID = "+userID+", address = "+address+", name = "+name+", longitude = "+longitude+", latitude = "+latitude+", checkedOut = "+checkedOut+"]";
    }
}
