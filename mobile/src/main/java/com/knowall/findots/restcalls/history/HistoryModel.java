package com.knowall.findots.restcalls.history;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by parijathar on 7/27/2016.
 */
@Parcel
public class HistoryModel {
    private String message;

    private int errorCode;

    @SerializedName("destinationsData")
    private HistoryData[] historyData;

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

    public HistoryData[] getHistoryData() {
        return historyData;
    }

    public void setHistoryData(HistoryData[] historyData) {
        this.historyData = historyData;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", errorCode = " + errorCode + ", historyData = " + historyData + "]";
    }
}
