package activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import findots.bridgetree.com.findots.R;
import utils.AppStringConstants;
import utils.GeneralUtils;

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

    Handler mHandlerSplash = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SPLASH_THREAD:
                    Intent intentNextActivity;

                    if((GeneralUtils.getSharedPreferenceInt(SplashActivity.this, AppStringConstants.USERID))>-1)
                    {
                        intentNextActivity = new Intent(SplashActivity.this,MenuActivity.class );
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
