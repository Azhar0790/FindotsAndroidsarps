package com.knowall.findots.restcalls.checkInCheckOut;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/22/2016.
 */
@Parcel
public class CheckInCheckOutModel {
    private String message;

    private String errorCode;

    @SerializedName("data")
    private CheckInCheckOutData[] mCheckInData;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getErrorCode ()
    {
        return errorCode;
    }

    public void setErrorCode (String errorCode)
    {
        this.errorCode = errorCode;
    }

    public CheckInCheckOutData[] getCheckInData ()
    {
        return mCheckInData;
    }

    public void setCheckInData (CheckInCheckOutData[] mCheckInData)
    {
        this.mCheckInData = mCheckInData;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", errorCode = "+errorCode+", mCheckInData = "+mCheckInData+"]";
    }
}
