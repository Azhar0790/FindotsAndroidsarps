package com.knowall.findots.distancematrix.model;

import org.parceler.Parcel;

/**
 * Created by parijathar on 7/12/2016.
 */
@Parcel
public class Rows {
    private Elements[] elements;

    public Elements[] getElements ()
    {
        return elements;
    }

    public void setElements (Elements[] elements)
    {
        this.elements = elements;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [elements = "+elements+"]";
    }
}
