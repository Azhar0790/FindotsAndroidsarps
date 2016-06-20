package restcalls.Login;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/17/2016.
 */
@Parcel
public class LoginData {
    private String base64Imagestring;

    private int countryID;

    private int userTypeID;

    private int organizationID;

    private String fbLoginID;

    private String redeemCode;

    private String googleID;

    private String isSubscription;

    private String city;

    private String userType;

    private int userID;

    private int corporateUserID;

    private String pinCode;

    private String email;

    private String address;

    private String name;

    private String company;

    private String imageURl;

    private String mobileNumber;

    public String getBase64Imagestring ()
    {
        return base64Imagestring;
    }

    public void setBase64Imagestring (String base64Imagestring)
    {
        this.base64Imagestring = base64Imagestring;
    }

    public int getCountryID ()
    {
        return countryID;
    }

    public void setCountryID (int countryID)
    {
        this.countryID = countryID;
    }

    public int getUserTypeID ()
    {
        return userTypeID;
    }

    public void setUserTypeID (int userTypeID)
    {
        this.userTypeID = userTypeID;
    }

    public int getOrganizationID ()
    {
        return organizationID;
    }

    public void setOrganizationID (int organizationID)
    {
        this.organizationID = organizationID;
    }

    public String getFbLoginID ()
    {
        return fbLoginID;
    }

    public void setFbLoginID (String fbLoginID)
    {
        this.fbLoginID = fbLoginID;
    }

    public String getRedeemCode ()
    {
        return redeemCode;
    }

    public void setRedeemCode (String redeemCode)
    {
        this.redeemCode = redeemCode;
    }

    public String getGoogleID ()
    {
        return googleID;
    }

    public void setGoogleID (String googleID)
    {
        this.googleID = googleID;
    }

    public String getIsSubscription ()
    {
        return isSubscription;
    }

    public void setIsSubscription (String isSubscription)
    {
        this.isSubscription = isSubscription;
    }

    public String getCity ()
    {
        return city;
    }

    public void setCity (String city)
    {
        this.city = city;
    }

    public String getUserType ()
    {
        return userType;
    }

    public void setUserType (String userType)
    {
        this.userType = userType;
    }

    public int getUserID ()
    {
        return userID;
    }

    public void setUserID (int userID)
    {
        this.userID = userID;
    }

    public int getCorporateUserID ()
    {
        return corporateUserID;
    }

    public void setCorporateUserID (int corporateUserID)
    {
        this.corporateUserID = corporateUserID;
    }

    public String getPinCode ()
    {
        return pinCode;
    }

    public void setPinCode (String pinCode)
    {
        this.pinCode = pinCode;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getAddress ()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCompany ()
    {
        return company;
    }

    public void setCompany (String company)
    {
        this.company = company;
    }

    public String getImageURl ()
    {
        return imageURl;
    }

    public void setImageURl (String imageURl)
    {
        this.imageURl = imageURl;
    }

    public String getMobileNumber ()
    {
        return mobileNumber;
    }

    public void setMobileNumber (String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [base64Imagestring = "+base64Imagestring+", countryID = "+countryID+", userTypeID = "+userTypeID+", organizationID = "+organizationID+", fbLoginID = "+fbLoginID+", redeemCode = "+redeemCode+", googleID = "+googleID+", isSubscription = "+isSubscription+", city = "+city+", userType = "+userType+", userID = "+userID+", corporateUserID = "+corporateUserID+", pinCode = "+pinCode+", email = "+email+", address = "+address+", name = "+name+", company = "+company+", imageURl = "+imageURl+", mobileNumber = "+mobileNumber+"]";
    }
}
