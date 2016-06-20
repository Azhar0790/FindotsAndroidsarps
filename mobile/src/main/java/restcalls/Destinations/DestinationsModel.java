package restcalls.Destinations;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/20/2016.
 */
@Parcel
public class DestinationsModel {
    private String message;

    private String errorCode;

    @SerializedName("data")
    private DestinationData[] mDestinationData;

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

    public DestinationData[] getDestinationData ()
    {
        return mDestinationData;
    }

    public void setData (DestinationData[] mDestinationData)
    {
        this.mDestinationData = mDestinationData;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", errorCode = "+errorCode+", mDestinationData = "+mDestinationData+"]";
    }
}
