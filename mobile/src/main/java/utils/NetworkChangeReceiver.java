package utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import findots.bridgetree.com.findots.R;

/**
 * Created by parijathar on 6/7/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    static NetworkInfo networkInfo = null;
    String toastInfo = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (isNetworkAvailable(context)) {
            toastInfo = context.getString(R.string.network_available) +
                    networkInfo.getExtraInfo() + " " + networkInfo.getTypeName();

            Toast.makeText(context, toastInfo, Toast.LENGTH_SHORT).show();
        } else {
            toastInfo = context.getString(R.string.network_not_available);

            Toast.makeText(context, toastInfo,Toast.LENGTH_SHORT).show();
        }
    }


    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }
}
