package activities;

import android.Manifest;
import android.support.v4.app.FragmentTransaction;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import adapters.MenuItemsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.FinDotsApplication;
import findots.bridgetree.com.findots.R;
import fragments.DestinationFragment;
import interfaces.IMenuItems;
import locationUtils.LocationRequestData;
import locationUtils.TrackLocationService;

public class MenuActivity extends RuntimePermissionActivity implements IMenuItems, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
private GoogleApiClient googleApiClient ;
private static final int REQUEST_RESOLVE_ERROR = 9999;
protected FinDotsApplication app;

    /**
     *  Menu Items Titles
     */
    String TITLES[] = { "Tracking Me",
                        "Destinations",
                        "Messages",
                        "Notifications",
                        "Settings",
                        "Help",
                        "Logout"};

    /**
     *   Action bar / App bar
     */
    private Toolbar mToolbar = null;

    /**
     *  Adapter to create Menu List Items
     */
    RecyclerView.Adapter mAdapter = null;

    /**
     *  LayoutManager as LinearLayoutManager
     */
    RecyclerView.LayoutManager mLayoutManager = null;

    /**
     *  Declaring DrawerLayout
     */
    @Bind(R.id.DrawerLayout_slider)
    DrawerLayout mDrawerLayout_slider = null;

    /**
     *  Action bar Drawer Toggle button
     */
    ActionBarDrawerToggle mToggle = null;

    @Bind(R.id.TextView_heading)
    TextView mTextView_heading;

    @Bind(R.id.RecyclerView_menu_items)
    RecyclerView mRecyclerView_menu_items;

    String userName = null;

    private static final int REQUEST_PERMISSIONS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        ButterKnife.bind(this);

        actionBarSettings();
        setViewForDashboard();

        mToggle = new ActionBarDrawerToggle(MenuActivity.this, mDrawerLayout_slider, mToolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout_slider.setDrawerListener(mToggle); // Drawer Listener set to the Drawer toggle
        mToggle.syncState();               // Finally we set the drawer toggle sync State

        mTextView_heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuActivity.this.requestAppPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        R.string.runtime_permissions_txt,
                        REQUEST_PERMISSIONS);
            }
        });
        app = (FinDotsApplication) getApplication();
        app.setLocationRequestData(LocationRequestData.FREQUENCY_HIGH);
        initalizeLocationService();
        /**
         *   Fragment Transaction for Destinations
         */

        FragmentTransaction destinationTransaction = getSupportFragmentManager().beginTransaction();
        destinationTransaction.replace(R.id.FrameLayout_content, DestinationFragment.newInstance());
        destinationTransaction.commit();

    }

    public void actionBarSettings() {

        /* Assigning the toolbar object ot the view
         * and setting the the Action bar to our toolbar
         */
        mToolbar = (Toolbar) findViewById(R.id.ToolBar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTextView_heading.setText(getString(R.string.tracking_list));
    }

    public void setViewForDashboard() {
        mRecyclerView_menu_items.setHasFixedSize(true);

        mAdapter = new MenuItemsAdapter(MenuActivity.this, getResources().getStringArray(R.array.menu_items), null, null, null);
        MenuItemsAdapter.delegate = MenuActivity.this;
        mRecyclerView_menu_items.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView_menu_items.setLayoutManager(mLayoutManager);
    }

    public void initalizeLocationService()
    {
        if(!(isMyServiceRunning(TrackLocationService.class)))
            startTracking();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMenuItemSelected(int itemPosition) {

        switch (itemPosition) {
            case Constants.TRACKING_ME:

                break;

            case Constants.DESTINATIONS:
                mDrawerLayout_slider.closeDrawer(Gravity.LEFT);

                FragmentTransaction destinationTransaction = getSupportFragmentManager().beginTransaction();
                destinationTransaction.replace(R.id.FrameLayout_content, DestinationFragment.newInstance());
                destinationTransaction.commit();
                break;

            case Constants.MESSAGES:

                break;

            case Constants.NOTIFICATIONS:

                break;

            case Constants.SETTINGS:

                break;

            case Constants.HELP:

                break;

            case Constants.LOGOUT:

                break;

            default:break;
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }
    private void showErrorDialog(int errorCode) {
        //TODO add errors handling
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt("dialog_error", errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    private void stopTracking() {
        stopService(new Intent(this, TrackLocationService.class));
    }
    private void startTracking() {
        connectGoogleApiClient();
    }

    private void startTrackLocationService() {
        Log.d("jomy", "onStartLocation...");
        startService(new Intent(this, TrackLocationService.class));
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("jomy", "onConnected");
        startTrackLocationService();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("jomy", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("jomy", "onConnectionFailed");
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {
            showErrorDialog(result.getErrorCode());
        }
    }

    private int createGoogleApiClient() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (status) {
            case ConnectionResult.SUCCESS:
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                break;
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, REQUEST_RESOLVE_ERROR);
                dialog.show();
                break;
        }
        return status;
    }

    private void connectGoogleApiClient() {
        if (googleApiClient == null) {
            if (createGoogleApiClient() != ConnectionResult.SUCCESS) {
                return;
            }
        }

        if (!(googleApiClient.isConnected() || googleApiClient.isConnecting())) {
            googleApiClient.connect();
        } else {
            Log.d("jomy", "Client is connected");
            startTrackLocationService();
        }
    }

}
