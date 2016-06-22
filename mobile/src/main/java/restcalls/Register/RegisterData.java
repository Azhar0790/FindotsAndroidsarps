package restcalls.register;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/17/2016.
 */
@Parcel
public class RegisterData {
    private String userID;

    private String Status;

    public String getUserID ()
    {
        return userID;
    }

    public void setUserID (String userID)
    {
        this.userID = userID;
    }

    public String getStatus ()
    {
        return Status;
    }

    public void setStatus (String Status)
    {
        this.Status = Status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [userID = "+userID+", Status = "+Status+"]";
    }
}
