package com.knowall.findots.restcalls.register;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/17/2016.
 */
@Parcel
public class RegisterData {
    private int userID;

    private String Status;

    private int userTypeID;

    private String userType;

    public int getUserTypeID() {
        return userTypeID;
    }

    public void setUserTypeID(int userTypeID) {
        this.userTypeID = userTypeID;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    @Override
    public String toString() {
        return "ClassPojo [userID = " + userID + ", Status = " + Status + "]";
    }
}
