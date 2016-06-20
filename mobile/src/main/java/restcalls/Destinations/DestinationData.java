package restcalls.Destinations;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/20/2016.
 */
@Parcel
public class DestinationData {
    private String destinationID;

    private String longitude;

    private String latitude;

    private String destinationName;

    public String getDestinationID ()
    {
        return destinationID;
    }

    public void setDestinationID (String destinationID)
    {
        this.destinationID = destinationID;
    }

    public String getLongitude ()
    {
        return longitude;
    }

    public void setLongitude (String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (String latitude)
    {
        this.latitude = latitude;
    }

    public String getDestinationName ()
    {
        return destinationName;
    }

    public void setDestinationName (String destinationName)
    {
        this.destinationName = destinationName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [destinationID = "+destinationID+", longitude = "+longitude+", latitude = "+latitude+", destinationName = "+destinationName+"]";
    }
}
