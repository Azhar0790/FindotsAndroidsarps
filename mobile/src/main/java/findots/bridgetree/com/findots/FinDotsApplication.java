package findots.bridgetree.com.findots;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import restservice.RestClient;

/**
 * Created by parijathar on 5/31/2016.
 */
public class FinDotsApplication extends MultiDexApplication {

    public static RestClient restClient = null;

    @Override
    public void onCreate() {
        super.onCreate();
        restClient = new RestClient();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        Log.i(Constants.TAG, "MultiDexApplication install is done");
    }

    public static RestClient getRestClient() {
        return restClient;
    }
}
