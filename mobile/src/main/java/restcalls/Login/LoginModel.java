package restcalls.Login;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/17/2016.
 */
@Parcel
public class LoginModel {
    private String message;

    private int errorCode;

    @SerializedName("data")
    private LoginData[] mLoginData;

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

    public LoginData[] getLoginData ()
    {
        return mLoginData;
    }

    public void setData (LoginData[] mLoginData)
    {
        this.mLoginData = mLoginData;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", errorCode = "+errorCode+", mLoginData = "+mLoginData+"]";
    }
}
