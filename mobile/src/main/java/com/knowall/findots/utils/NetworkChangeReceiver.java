package com.knowall.findots.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.knowall.findots.R;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.database.dataModel.checkIn;
import com.knowall.findots.offlineServices.CheckInOutOfflineService;

import java.util.List;

/**
 * Created by parijathar on 6/7/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    static NetworkInfo networkInfo = null;
    String toastInfo = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (isNetworkAvailable(context)) {

            if (networkInfo.getExtraInfo() != null) {
                toastInfo = context.getString(R.string.network_available) + " "+
                        networkInfo.getExtraInfo() + " " + networkInfo.getTypeName();
                callCheckInOffline(context);
            } else {
                toastInfo = context.getString(R.string.noInternet);
            }

            Toast.makeText(context, toastInfo, Toast.LENGTH_SHORT).show();
        } else {
            toastInfo = context.getString(R.string.noInternet);
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

    public static void callCheckInOffline(Context context)
    {
        DataHelper dataHelper = DataHelper.getInstance(context);
        List<checkIn> checkInList = dataHelper.getCheckInListToSync();


        if(checkInList.size()>0)
        {
            Intent intent = new Intent(context, CheckInOutOfflineService.class);
            intent.putExtra("checkinStatus", false);
            context.startService(intent);
        }
        List<checkIn> checkOutList = dataHelper.getCheckOutListToSync();
        if(checkOutList.size()>0)
        {
            Intent intent = new Intent(context, CheckInOutOfflineService.class);
            intent.putExtra("checkinStatus", true);
            context.startService(intent);
        }


    }


}
