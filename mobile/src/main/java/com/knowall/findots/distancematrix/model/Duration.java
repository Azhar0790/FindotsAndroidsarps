package com.knowall.findots.distancematrix.model;

import org.parceler.Parcel;

/**
 * Created by parijathar on 7/12/2016.
 */
@Parcel
public class Duration {
    private String text;

    private String value;

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [text = "+text+", value = "+value+"]";
    }
}
