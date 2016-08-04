package com.knowall.findots.restcalls.history;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by parijathar on 7/27/2016.
 */
@Parcel
public class HistoryModel {
    private String message;

    private int errorCode;

    public ArrayList<HistoryData> getHistoryData() {
        return historyData;
    }

    public void setHistoryData(ArrayList<HistoryData> historyData) {
        this.historyData = historyData;
    }

    @SerializedName("destinationsData")
    private ArrayList<HistoryData> historyData =new ArrayList<HistoryData>();

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
        return "ClassPojo [message = " + message + ", errorCode = " + errorCode + ", historyData = " + historyData + "]";
    }
}
