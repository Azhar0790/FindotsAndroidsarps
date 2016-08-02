package com.knowall.findots.restcalls.joinTeam;

import org.parceler.Parcel;

/**
 * Created by parijathar on 8/2/2016.
 */
@Parcel
public class JoinTeamModel {
    private String message;

    private int errorCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", errorCode = " + errorCode + "]";
    }
}
