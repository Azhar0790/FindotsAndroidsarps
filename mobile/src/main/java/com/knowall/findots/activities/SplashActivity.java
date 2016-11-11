package com.knowall.findots.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

/**
 * Created by parijathar on 6/6/2016.
 */
public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_THREAD = 0;
    public static int DELAY_TIME = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Message message = new Message();
        message.what = SPLASH_THREAD;
        SplashActivity.this.mHandlerSplash.sendMessageDelayed(message, DELAY_TIME);

    }

    @Override
    protected void onResume() {
        super.onResume();
        FinDotsApplication.getInstance().trackScreenView("Splash Screen");
    }

    Handler mHandlerSplash = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SPLASH_THREAD:
                    Intent intentNextActivity;

                    if((GeneralUtils.getSharedPreferenceInt(SplashActivity.this, AppStringConstants.USERID))>-1)
                    {
//                        int userTypeID = GeneralUtils.getSharedPreferenceInt(SplashActivity.this, AppStringConstants.USER_TYPE_ID);
//                        if (userTypeID != LoginActivity.CORPORATE_ADMIN)

                        // If a notification message is tapped, any data accompanying the notification
                        // message is available in the intent extras. In this sample the launcher
                        // intent is fired when the notification is tapped, so any accompanying data would
                        // be handled here. If you want a different intent fired, set the click_action
                        // field of the notification message to the desired intent. The launcher intent
                        // is used when no click_action is specified.
                            intentNextActivity = new Intent(SplashActivity.this,MenuActivity.class );
                        if (getIntent().getExtras() != null) {

                            for (String key : getIntent().getExtras().keySet()) {
                                Object value = getIntent().getExtras().get(key);
                                if(key.equals("body"))
                                    intentNextActivity.putExtra("body",""+value);
                                else if(key.equals("title"))
                                    intentNextActivity.putExtra("title",""+value.toString());
                            }
                        }
                    }
                    else
                    {
                        intentNextActivity = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(intentNextActivity);
                    finish();
                    break;

                default:break;
            }
        }
    };
}
