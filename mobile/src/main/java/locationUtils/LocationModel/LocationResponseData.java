package locationUtils.LocationModel;

/**
 * Created by jpaulose on 6/16/2016.
 */
public class LocationResponseData {

    private String message;

    private String errorCode;

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
}
