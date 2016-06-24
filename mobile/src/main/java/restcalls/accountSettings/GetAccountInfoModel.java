package restcalls.accountSettings;

import java.util.ArrayList;

/**
 * Created by jpaulose on 6/23/2016.
 */
public class GetAccountInfoModel {

    private String message;
    private int  errorCode;
    private ArrayList<AccountInfoData> data=new ArrayList<AccountInfoData>();


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }


    public ArrayList<AccountInfoData> getData() {
        return data;
    }

    public void setData(ArrayList<AccountInfoData> data) {
        this.data = data;
    }



    public class AccountInfoData
    {
      private String name;

        private String email;
        private String mobileNumber;
        private String city;
        private String pinCode;
        private String address;
        private String company;
        private String userTypeID;
        private String userType;
        private String organizationID;
        private String countryID;
        private String imageURl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPinCode() {
            return pinCode;
        }

        public void setPinCode(String pinCode) {
            this.pinCode = pinCode;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getUserTypeID() {
            return userTypeID;
        }

        public void setUserTypeID(String userTypeID) {
            this.userTypeID = userTypeID;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getOrganizationID() {
            return organizationID;
        }

        public void setOrganizationID(String organizationID) {
            this.organizationID = organizationID;
        }

        public String getCountryID() {
            return countryID;
        }

        public void setCountryID(String countryID) {
            this.countryID = countryID;
        }

        public String getImageURl() {
            return imageURl;
        }

        public void setImageURl(String imageURl) {
            this.imageURl = imageURl;
        }
    }
}
