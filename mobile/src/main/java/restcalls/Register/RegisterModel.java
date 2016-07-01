package restcalls.register;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/17/2016.
 */
@Parcel
public class RegisterModel {
    private String message;

    private int errorCode;

    @SerializedName("data")
    private RegisterData[] mRegisterData;

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
