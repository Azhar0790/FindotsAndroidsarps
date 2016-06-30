package offlineServices;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by jpaulose on 6/29/2016.
 */
public class CheckInOutOfflineService extends IntentService
{
    private static final String TAG = "CheckInOutOfflineService";

    public CheckInOutOfflineService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {

    }

}
