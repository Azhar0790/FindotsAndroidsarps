package com.knowall.findots.restcalls.getUser;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by parijathar on 8/9/2016.
 */
@Parcel
public class GetUserModel {
    private String message;

    private int errorCode;

    @SerializedName("data")
    private GetUserData[] getUserDatas;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public int getErrorCode ()
    {
        return errorCode;
    }

    public void setErrorCode (int errorCode)
    {
        this.errorCode = errorCode;
    }

    public GetUserData[] getUserData ()
    {
        return getUserDatas;
    }

    public void setData (GetUserData[] getUserDatas)
    {
        this.getUserDatas = getUserDatas;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", errorCode = "+errorCode+", getUserDatas = "+getUserDatas+"]";
    }
}
