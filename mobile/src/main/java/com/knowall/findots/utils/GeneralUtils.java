package com.knowall.findots.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.knowall.findots.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jpaulose on 6/15/2016.
 */
public class GeneralUtils {

    public static String LatLongToAddress(Double latitude, Double longitude, Context context) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";
        Log.d("jomy", "addr111 : ");
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String addressLine1 = addresses.get(0).getAddressLine(0);
            if (addressLine1 != null)
                address = address + " " + addressLine1;
            String addressLine2 = addresses.get(0).getAddressLine(1);
            if (addressLine2 != null)
                address = address + " " + addressLine2;
            String Street = addresses.get(0).getLocality();
            if (Street != null)
                address = address + " " + Street;
            String state = addresses.get(0).getAdminArea();
            if (state != null)
                address = address + " " + state;
            String country = addresses.get(0).getCountryName();
            if (country != null)
                address = address + " " + country;
            String postalCode = addresses.get(0).getPostalCode();
            if (postalCode != null)
                address = address + " " + postalCode;
        } catch (Exception e) {
        }

        Log.d("jomy", "addr2 : " + address);
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

    /**
     * Email ID validation
     */
    public static boolean isvalid_email(String email) {

        String regEx_email = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        if (email.length() == 0) {
            return false;
        }

        Matcher matcherObj_email = Pattern.compile(regEx_email).matcher(
                email);

        return matcherObj_email.matches();
    }

    public static String getUniqueDeviceId(Context context) {
        String myAndroidDeviceId = "";
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            myAndroidDeviceId = mTelephony.getDeviceId();
        } else {
            myAndroidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return myAndroidDeviceId;
    }

    public static String getDeviceInfo() {
        String myAndroidDeviceinfo = "";

        myAndroidDeviceinfo = android.os.Build.BRAND;
        myAndroidDeviceinfo = myAndroidDeviceinfo + " " + android.os.Build.MODEL;
        myAndroidDeviceinfo = myAndroidDeviceinfo + " " + android.os.Build.VERSION.RELEASE;

        return myAndroidDeviceinfo;
    }

    public static String getAppVersion(Context context) {
        String versionCode = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String versionName = packageInfo.versionName;

            versionCode = Integer.toString(packageInfo.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getSharedPreferenceString(Context context, String prefKey) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String preferenceVal = pref.getString(prefKey, "");
        return preferenceVal;
    }

    public static int getSharedPreferenceInt(Context context, String prefKey) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int preferenceVal = pref.getInt(prefKey, -1);
        return preferenceVal;
    }

    public static void setSharedPreferenceString(Context context, String prefKey, String prefVal) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(prefKey, prefVal).apply();

    }

    public static void setSharedPreferenceInt(Context context, String prefKey, int prefVal) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(prefKey, prefVal).apply();

    }

    public static void removeSharedPreference(Context context, String prefkey) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().remove(prefkey).commit();
    }


    public static Drawable scaleDrawable(Drawable drawable, int width, int height) {

        int wi = drawable.getIntrinsicWidth();
        int hi = drawable.getIntrinsicHeight();
        int dimDiff = Math.abs(wi - width) - Math.abs(hi - height);
        float scale = (dimDiff > 0) ? width / (float) wi : height /
                (float) hi;
        Rect bounds = new Rect(0, 0, (int) (scale * wi), (int) (scale * hi));
        drawable.setBounds(bounds);
        return drawable;
    }

    public static boolean checkPlayServices(Context context) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog((Activity) context,
                        resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(context, "Location not supported in this device.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }


}
