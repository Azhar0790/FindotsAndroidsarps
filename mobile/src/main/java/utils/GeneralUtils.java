package utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.app.AlertDialog;
import android.view.Window;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import findots.bridgetree.com.findots.R;

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

    /**
     * Initializes the Progress bar
     */
    static Dialog mDialog = null;

    public static void initialize_progressbar(Context context) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_progress);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    /**
     * Dismisses the Progress bar
     */
    public static void stop_progressbar() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public static void createAlertDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
