package restcalls.Register;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/17/2016.
 */
@Parcel
public class RegisterModel {
    private String message;

    private String errorCode;

    @SerializedName("Data")
    private RegisterData[] mRegisterData;

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

    public RegisterData[] getRegisterData ()
    {
        return mRegisterData;
    }

    public void setRegisterData (RegisterData[] mRegisterData)
    {
        this.mRegisterData = mRegisterData;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", errorCode = "+errorCode+", mRegisterData = "+mRegisterData+"]";
    }
}
