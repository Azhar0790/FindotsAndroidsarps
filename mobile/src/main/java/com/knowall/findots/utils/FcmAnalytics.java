package com.knowall.findots.utils;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by jpaulose on 11/10/2016.
 */
public class FcmAnalytics {

    public static FirebaseAnalytics mFirebaseAnalytics;

    public static FirebaseAnalytics getInstance(Context mContext)
    {
      if(mFirebaseAnalytics==null)
          mFirebaseAnalytics= FirebaseAnalytics.getInstance(mContext);
        return mFirebaseAnalytics;
    }

}
