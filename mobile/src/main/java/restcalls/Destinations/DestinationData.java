package restcalls.destinations;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/20/2016.
 */
@Parcel
public class DestinationData {
    private int destinationID;

    private String checkedOutReportedDate;

    private String address;

    private String assigndestinationTime;

    private String name;

    private boolean checkedIn;

    private String checkedInReportedDate;

    private double destinationLongitude;

    private String destinationName;

    private double destinationLatitude;

    private boolean checkedOut;

    public int getDestinationID ()
    {
        return destinationID;
    }

    public void setDestinationID (int destinationID)
    {
        this.destinationID = destinationID;
    }

    public String getCheckedOutReportedDate ()
    {
        return checkedOutReportedDate;
    }

    public void setCheckedOutReportedDate (String checkedOutReportedDate)
    {
        this.checkedOutReportedDate = checkedOutReportedDate;
    }

    public String getAddress ()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }

    public String getAssigndestinationTime ()
    {
        return assigndestinationTime;
    }

    public void setAssigndestinationTime (String assigndestinationTime)
    {
        this.assigndestinationTime = assigndestinationTime;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public boolean getCheckedIn ()
    {
        return checkedIn;
    }

    public void setCheckedIn (boolean checkedIn)
    {
        this.checkedIn = checkedIn;
    }

    public String getCheckedInReportedDate ()
    {
        return checkedInReportedDate;
    }

    public void setCheckedInReportedDate (String checkedInReportedDate)
    {
        this.checkedInReportedDate = checkedInReportedDate;
    }

    public double getDestinationLongitude ()
    {
        return destinationLongitude;
    }

    public void setDestinationLongitude (double destinationLongitude)
    {
        this.destinationLongitude = destinationLongitude;
    }

    public String getDestinationName ()
    {
        return destinationName;
    }

    public void setDestinationName (String destinationName)
    {
        this.destinationName = destinationName;
    }

    public double getDestinationLatitude ()
    {
        return destinationLatitude;
    }

    public void setDestinationLatitude (double destinationLatitude)
    {
        this.destinationLatitude = destinationLatitude;
    }

    public boolean getCheckedOut ()
    {
        return checkedOut;
    }

    public void setCheckedOut (boolean checkedOut)
    {
        this.checkedOut = checkedOut;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [destinationID = "+destinationID+", checkedOutReportedDate = "+checkedOutReportedDate+", address = "+address+", assigndestinationTime = "+assigndestinationTime+", name = "+name+", checkedIn = "+checkedIn+", checkedInReportedDate = "+checkedInReportedDate+", destinationLongitude = "+destinationLongitude+", destinationName = "+destinationName+", destinationLatitude = "+destinationLatitude+", checkedOut = "+checkedOut+"]";
    }
}
