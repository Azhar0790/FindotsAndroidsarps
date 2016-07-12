package com.knowall.findots.restcalls.checkInCheckOut;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/22/2016.
 */
@Parcel
public class CheckInCheckOutData {
    private String status;

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [status = "+status+"]";
    }
}
