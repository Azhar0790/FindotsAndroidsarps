package locationUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static float distFromCoordinates(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        return dist;
    }

    public static String formatTime(long milis) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date(milis));
        return time;
    }

    public static String formatTimeWithoutSeconds(long milis) {
        String time = new SimpleDateFormat("HH:mm").format(new Date(milis));
        return time;
    }

    public static String formatDate(long milis) {
        String time = new SimpleDateFormat("MMMM dd, yyyy").format(new Date(milis));
        return time;
    }

    public static String formatDateAndTime(long milis) {
        String time = new SimpleDateFormat("HH:mm, dd 'of' MMMM yyyy").format(new Date(milis));
        return time;
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

    public static boolean isLocationServiceEnabled(Context context){
        int locationMode = 0;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            LocationManager locationManager = null;
            boolean gps_enabled= false,network_enabled = false;

            if(locationManager ==null)
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            try{
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }catch(Exception ex){
                //do nothing...
            }

            try{
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }catch(Exception ex){
                //do nothing...
            }

            return gps_enabled || network_enabled;
        }

    }

    public static void createLocationServiceError(final Context context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Enable Location Service");
        builder.setMessage("Please enable location Service");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


}
