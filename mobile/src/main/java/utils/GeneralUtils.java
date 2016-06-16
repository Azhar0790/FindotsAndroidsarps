package utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jpaulose on 6/15/2016.
 */
public class GeneralUtils {

    public static String DateTimeInGMT()
    {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        //Time in GMT
        return dateFormatGmt.format(new Date());
    }
    public static String LatLongToAddress(Double latitude,Double longitude,Context context)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String address="";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String addressLine1=addresses.get(0).getAddressLine(0);
            if(addressLine1!=null)
                address =address+" "+addressLine1;
            String addressLine2=addresses.get(0).getAddressLine(1);
            if(addressLine2!=null)
                address =address+" "+addressLine2;
            String Street=addresses.get(0).getLocality();
            if(Street!=null)
                address =address+" "+Street;
            String state=addresses.get(0).getAdminArea();
            if(state!=null)
                address =address+" "+state;
            String country=addresses.get(0).getCountryName();
            if(country!=null)
                address =address+" "+country;
            String postalCode =addresses.get(0).getPostalCode();
            if(postalCode!=null)
                address =address+" "+postalCode;
        }
        catch (Exception e){}
        return address;
    }

}
