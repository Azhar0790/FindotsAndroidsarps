package com.knowall.findots.locationUtils.LocationModel;

/**
 * Created by jpaulose on 6/16/2016.
 */
public class LocationResponseData {

    private String message;

    private int errorCode;

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
}
