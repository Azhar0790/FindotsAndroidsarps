package activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapters.MenuItemsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import database.DataHelper;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.FinDotsApplication;
import findots.bridgetree.com.findots.R;
import fragments.Account_Settings;
import fragments.DestinationsTabFragment;
import interfaces.IMenuItems;
import locationUtils.LocationModel.BackgroundLocData;
import locationUtils.LocationModel.LocationSyncData;
import locationUtils.LocationRequestData;
import locationUtils.TrackLocationService;
import locationUtils.Utils;
import restmodels.ResponseModel;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utils.AppStringConstants;
import utils.GeneralUtils;

public class MenuActivity extends RuntimePermissionActivity implements IMenuItems, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_RESOLVE_ERROR = 9999;
    protected FinDotsApplication app;
    boolean locationReport = false;

    /**
     * Menu Items Titles
     */
    int ICONS[] = {
            R.drawable.destinations,
            R.drawable.menu_report_loc,
            R.drawable.settings,
            R.drawable.help,
            R.drawable.logout};

    /**
     * Action bar / App bar
     */
    private Toolbar mToolbar = null;

    /**
     * Adapter to create Menu List Items
     */
    RecyclerView.Adapter mAdapter = null;

    /**
     * LayoutManager as LinearLayoutManager
     */
    RecyclerView.LayoutManager mLayoutManager = null;

    /**
     * Declaring DrawerLayout
     */
    @Bind(R.id.DrawerLayout_slider)
    DrawerLayout mDrawerLayout_slider = null;

    /**
     * Action bar Drawer Toggle button
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

        if(!(Utils.isLocationServiceEnabled(this))) {
            Utils.createLocationServiceError(this);
        }
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

                int account_update = GeneralUtils.getSharedPreferenceInt(MenuActivity.this,
                        AppStringConstants.ACCOUNT_UPDATE);
                if (account_update == 1) {
                    setViewForDashboard();
                    GeneralUtils.setSharedPreferenceInt(MenuActivity.this, AppStringConstants.ACCOUNT_UPDATE, 0);
                }

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

        mToggle.setDrawerIndicatorEnabled(true);
        FragmentTransaction destinationTransaction = getSupportFragmentManager().beginTransaction();
        destinationTransaction.replace(R.id.FrameLayout_content, DestinationsTabFragment.newInstance());
        destinationTransaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        FinDotsApplication.getInstance().trackScreenView("Home Screen");
    }

    public void actionBarSettings() {

        /* Assigning the toolbar object ot the view
         * and setting the the Action bar to our toolbar
         */
        mToolbar = (Toolbar) findViewById(R.id.ToolBar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typefaceMyriadHebrew = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        mTextView_heading.setText(getString(R.string.destinations));
        mTextView_heading.setTypeface(typefaceMyriadHebrew);
    }

    public void setViewForDashboard() {
        mRecyclerView_menu_items.setHasFixedSize(true);

        String name = GeneralUtils.getSharedPreferenceString(MenuActivity.this, AppStringConstants.NAME);

        mAdapter = new MenuItemsAdapter(MenuActivity.this, getResources().getStringArray(R.array.menu_items), ICONS, name, null);
        MenuItemsAdapter.delegate = MenuActivity.this;
        mRecyclerView_menu_items.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView_menu_items.setLayoutManager(mLayoutManager);
    }

    public void initalizeLocationService() {
        if (!(isMyServiceRunning(TrackLocationService.class)))
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

            case Constants.DESTINATIONS:
                mDrawerLayout_slider.closeDrawer(Gravity.LEFT);
                mTextView_heading.setText(R.string.destinations);
                findViewById(R.id.FrameLayout_content).setVisibility(View.GONE);

                FragmentTransaction destinationTransaction = getSupportFragmentManager().beginTransaction();
                destinationTransaction.replace(R.id.FrameLayout_content, DestinationsTabFragment.newInstance());
                destinationTransaction.commit();

                findViewById(R.id.FrameLayout_content).setVisibility(View.VISIBLE);
                break;

            case Constants.TRACKLOCATION:
                mDrawerLayout_slider.closeDrawer(Gravity.LEFT);
                locationReport = true;

                connectGoogleApiClient();

                break;


            case Constants.USERINFO:
            case Constants.ACCOUNT_SETTINGS:
                mDrawerLayout_slider.closeDrawer(Gravity.LEFT);
                mTextView_heading.setText(R.string.account_heading);
                findViewById(R.id.FrameLayout_content).setVisibility(View.GONE);

                FragmentTransaction accountTransaction = getSupportFragmentManager().beginTransaction();
                accountTransaction.replace(R.id.FrameLayout_content, Account_Settings.newInstance());
                accountTransaction.commit();

                findViewById(R.id.FrameLayout_content).setVisibility(View.VISIBLE);


                break;

            case Constants.HELP:
                mDrawerLayout_slider.closeDrawer(Gravity.LEFT);
//                FinDotsApplication.getDatabaseInfo(this);
                Intent mailIntent = new Intent();
                mailIntent.setAction(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse("mailto:"));
//                mailIntent.setType("message/rfc822");
                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@findots.com"});
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help Request");
                try {
                    startActivity(Intent.createChooser(mailIntent, "Send Help Email"));
                } catch (ActivityNotFoundException e) {
                    //TODO: Handle case where no email app is available
                }

                break;
            case Constants.LOGOUT:
                logoutConfirmation();
                break;

            default:
                break;
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    public void logoutConfirmation() {
        new AlertDialog.Builder(MenuActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.logout_confirmation))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logOut();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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
//        Log.d("jomy", "onConnected");
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
            if (locationReport) {
                ReportMyLocation(LocationServices.FusedLocationApi
                        .getLastLocation(googleApiClient));
                locationReport = false;
            } else {
                startTrackLocationService();
            }
        }
    }

    public void ReportMyLocation(Location currentLoc) {
        GeneralUtils.initialize_progressbar(this);
        BackgroundLocData bgData = new BackgroundLocData();
        bgData.setDeviceID(GeneralUtils.getUniqueDeviceId(this));
        bgData.setDeviceInfo(GeneralUtils.getDeviceInfo());
//        bgData.setAppVersion(GeneralUtils.getAppVersion(this));
        bgData.setAppVersion("5.0");
        bgData.setUserID(GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));
        bgData.setDeviceTypeID(Constants.DEVICETYPEID);
        ArrayList<LocationSyncData> locSyncList = new ArrayList<LocationSyncData>();
        LocationSyncData locationSyncData = new LocationSyncData();
        locationSyncData.setLatitude(currentLoc.getLatitude());
        locationSyncData.setLongitude(currentLoc.getLongitude());
        locationSyncData.setReportedDate(GeneralUtils.DateTimeInUTC());
        locSyncList.add(locationSyncData);


        bgData.setLocations(locSyncList);

        /**
         * Hit the Login API to get the Userdetails
         */

        Log.d("jomy", "getUserID() : " + bgData.getUserID());

        Call<ResponseModel> reportLocationCall = FinDotsApplication.getRestClient().getApiService().saveLocationPath(bgData);

        reportLocationCall.enqueue(new Callback<ResponseModel>() {

            @Override
            public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                Log.d("jomy", "sucessReportLoc... ");
                if (response.isSuccess() && response.body().getErrorCode() == 0) {
                    Toast.makeText(MenuActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MenuActivity.this, getResources().getString(R.string.report_loc_fail), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                Toast.makeText(MenuActivity.this, getResources().getString(R.string.report_loc_fail), Toast.LENGTH_SHORT).show();
                Log.d("jomy", "Failure... ");
            }
        });
    }

    public void logOut() {
        GeneralUtils.initialize_progressbar(this);
        Map<String, Object> postValues = new HashMap<>();

        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(this));
        postValues.put("appVersion", GeneralUtils.getAppVersion(this));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("ipAddress", "");
        postValues.put("userID", GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));

        Call<ResponseModel> call = FinDotsApplication.getRestClient().getApiService().logOut(postValues);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                logOutNavigation();

                if (response.isSuccess() & response.body().getErrorCode()== 0) {
                    Toast.makeText(MenuActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MenuActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                logOutNavigation();

            }
        });

    }

    public void logOutNavigation() {
        GeneralUtils.stop_progressbar();
        stopTracking();
        DataHelper dataHelper = DataHelper.getInstance(this);
        dataHelper.deleteAllLocations();
        dataHelper.deleteCheckinList();
        GeneralUtils.removeSharedPreference(MenuActivity.this, AppStringConstants.USERID);
        Intent intentLogout = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intentLogout);
        finish();
    }
}
