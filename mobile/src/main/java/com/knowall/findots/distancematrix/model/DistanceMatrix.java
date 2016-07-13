package com.knowall.findots.distancematrix.model;

import org.parceler.Parcel;

/**
 * Created by parijathar on 7/12/2016.
 */
@Parcel
public class DistanceMatrix {
    private String status;

    private String[] destination_addresses;

    private String[] origin_addresses;

    private Rows[] rows;

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String[] getDestination_addresses ()
    {
        return destination_addresses;
    }

    public void setDestination_addresses (String[] destination_addresses)
    {
        this.destination_addresses = destination_addresses;
    }

    public String[] getOrigin_addresses ()
    {
        return origin_addresses;
    }

    public void setOrigin_addresses (String[] origin_addresses)
    {
        this.origin_addresses = origin_addresses;
    }

    public Rows[] getRows ()
    {
        return rows;
    }

    public void setRows (Rows[] rows)
    {
        this.rows = rows;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [status = "+status+", destination_addresses = "+destination_addresses+", origin_addresses = "+origin_addresses+", rows = "+rows+"]";
    }
}
