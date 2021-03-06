package com.knowall.findots.activities;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.adapters.MenuItemsAdapter;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.fragments.Account_Settings;
import com.knowall.findots.fragments.InitialFragment;
import com.knowall.findots.interfaces.IMenuItems;
import com.knowall.findots.locationUtils.LocationModel.BackgroundLocData;
import com.knowall.findots.locationUtils.LocationModel.LocationSyncData;
import com.knowall.findots.locationUtils.LocationRequestData;
import com.knowall.findots.locationUtils.TrackLocationService;
import com.knowall.findots.locationUtils.Utils;
import com.knowall.findots.restcalls.joinTeam.IJoinTeam;
import com.knowall.findots.restcalls.joinTeam.JoinTeamRestCall;
import com.knowall.findots.restmodels.ResponseModel;
import com.knowall.findots.utils.AddTextWatcher;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.FcmAnalytics;
import com.knowall.findots.utils.GeneralUtils;
import com.knowall.findots.utils.NetworkChangeReceiver;
import com.knowall.findots.utils.timeUtils.TimeSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MenuActivity extends RuntimePermissionActivity
        implements
        IMenuItems,
        IJoinTeam,
        GoogleApiClient.ConnectionCallbacks,
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
            R.drawable.join_team,
            R.drawable.settings,
            R.drawable.help,
            R.drawable.logout};

    /**
     * Menu Items for Corporate Admin
     */
    int AdminICONS[] = {
            R.drawable.destinations,
            R.drawable.settings,
            R.drawable.help,
            R.drawable.logout};


    /**
     * User Types
     */
    public static final int INDIVIDUAL = 1;
    public static final int CORPORATE = 2;
    public static final int CORPORATE_ADMIN = 3;
    public static final int WEB_USER_ADMIN = 4;
    public static final int COUNTRY_SPECIFIC_ADMIN = 5;

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
    private static final int REQUEST_PERMISSIONS = 20;
    public static Context ContextMenuActivity = null;
    Bundle bundle = null;
    boolean fromRegister = false;
    int userTypeID = 0;

    public static int device_width;
    DisplayMetrics metrics = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        metrics = this.getResources().getDisplayMetrics();
        device_width = metrics.widthPixels;

        if (getIntent().getExtras() != null) {
            showNotificationDialog(getIntent().getStringExtra("title"),getIntent().getStringExtra("body"));
            Log.d("jomy", "bodyvv: " + getIntent().getStringExtra("body")+" Keyvv : "+getIntent().getStringExtra("title") );
        }
        userTypeID = GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USER_TYPE_ID);

        if (userTypeID == CORPORATE_ADMIN) {
            int adminID = GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.ADMIN_ID);
            GeneralUtils.setSharedPreferenceInt(this, AppStringConstants.USERID, adminID);
        }

        bundle = getIntent().getExtras();

        if (bundle != null) {
            fromRegister = bundle.getBoolean("fromRegister");
        }

        ContextMenuActivity = MenuActivity.this;

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
        app.setLocationRequestData(LocationRequestData.FREQUENCY_MEDIUM);
        initalizeLocationService();

        mToggle.setDrawerIndicatorEnabled(true);

        setHeading();

        setInitialFragmentView();

    }

    public void setInitialFragmentView() {
        switch (userTypeID) {
            case CORPORATE:
                if (fromRegister) {
                    showJoinATeamDialog(true);
                } else {
                    FragmentTransaction initialFragment = getSupportFragmentManager().beginTransaction();
                    initialFragment.replace(R.id.FrameLayout_content, InitialFragment.newInstance());
                    initialFragment.commit();
                }
                break;

            case CORPORATE_ADMIN:
                FragmentTransaction initialFragment = getSupportFragmentManager().beginTransaction();
                initialFragment.replace(R.id.FrameLayout_content, InitialFragment.newInstance());
                initialFragment.commit();
                break;

            default:
                if (fromRegister) {
                    showJoinATeamDialog(true);
                } else {
                    FragmentTransaction initialFragment1 = getSupportFragmentManager().beginTransaction();
                    initialFragment1.replace(R.id.FrameLayout_content, InitialFragment.newInstance());
                    initialFragment1.commit();
                }
        }
    }

    public void setHeading() {
        switch (userTypeID) {
            case CORPORATE:
                mTextView_heading.setText(getString(R.string.destinations));
                break;

            case CORPORATE_ADMIN:
                mTextView_heading.setText(getString(R.string.all));
                break;

            default:
                mTextView_heading.setText(getString(R.string.destinations));
                break;
        }
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
        mTextView_heading.setTypeface(typefaceMyriadHebrew);


    }

    void showNotificationDialog(String title,String body)
    {
        if(title!=null && body !=null)
        {
            new AlertDialog.Builder(MenuActivity.this)
                    .setTitle(title)
                    .setMessage(body)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }
    public void setViewForDashboard() {
        mRecyclerView_menu_items.setHasFixedSize(true);

        String name = GeneralUtils.getSharedPreferenceString(MenuActivity.this, AppStringConstants.NAME);

        String menuItems[] = /*getResources().getStringArray(R.array.menu_items)*/null;
        int icons[] = null;

        switch (userTypeID) {
            case CORPORATE:
                menuItems = getResources().getStringArray(R.array.menu_items);
                icons = ICONS;
                break;

            case CORPORATE_ADMIN:
                menuItems = getResources().getStringArray(R.array.admin_menu_items);
                icons = AdminICONS;
/*
                mAdapter = new MenuItemsAdapter(MenuActivity.this, getResources().getStringArray(R.array.admin_menu_items), AdminICONS, name, null);
                MenuItemsAdapter.delegate = MenuActivity.this;
                mRecyclerView_menu_items.setAdapter(mAdapter);*/
                break;

            default:
//                menuItems = new String[]{};
//                icons = new int[]{};
                menuItems = getResources().getStringArray(R.array.menu_items);
                icons = ICONS;
                break;
        }

        mAdapter = new MenuItemsAdapter(MenuActivity.this, menuItems, icons, name, null);
        MenuItemsAdapter.delegate = MenuActivity.this;
        mRecyclerView_menu_items.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView_menu_items.setLayoutManager(mLayoutManager);
    }

    public void initalizeLocationService() {
        if (!(isMyServiceRunning(TrackLocationService.class)))
            startTracking();
        else
            connectGoogleApiClient();
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
                findViewById(R.id.FrameLayout_content).setVisibility(View.GONE);

                /*if (userTypeID == 3) {
                    mTextView_heading.setText("All");

                } else {
                    mTextView_heading.setText(R.string.destinations);

                    FragmentTransaction destinationTransaction = getSupportFragmentManager().beginTransaction();
                    destinationTransaction.replace(R.id.FrameLayout_content, DestinationsTabFragment.newInstance());
                    destinationTransaction.commit();
                }*/
                setHeading();

                FragmentTransaction initialFragment = getSupportFragmentManager().beginTransaction();
                initialFragment.replace(R.id.FrameLayout_content, InitialFragment.newInstance());
                initialFragment.commit();

                findViewById(R.id.FrameLayout_content).setVisibility(View.VISIBLE);
                //Fcm Analytics
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Destinations");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Destinations"+ " is opened");
                FcmAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//                FcmAnalytics.getInstance(this).setUserProperty("userDevice", ""+ CommonUtils.DeviceInfo);
                break;

            case Constants.TRACKLOCATION:
                mDrawerLayout_slider.closeDrawer(Gravity.LEFT);
                locationReport = true;
                connectGoogleApiClient();

                break;

            case Constants.JOIN_A_TEAM:
                mDrawerLayout_slider.closeDrawer(Gravity.LEFT);
                showJoinATeamDialog(false);
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
                FinDotsApplication.getDatabaseInfo(this);
                Intent mailIntent = new Intent();
                mailIntent.setAction(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse("mailto:"));
//                mailIntent.setType("message/rfc822");
                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@findots.com"});
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help Request Email");
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

    private void showJoinATeamDialog(final boolean isDestinations) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MenuActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_user_input, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MenuActivity.this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText mEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        mEditText.addTextChangedListener(new AddTextWatcher(mEditText));

        TextView dialogTitle = (TextView) mView.findViewById(R.id.dialogTitle);
        dialogTitle.setText(getResources().getString(R.string.join_team));
        dialogTitle.setVisibility(View.VISIBLE);

        mEditText.setHint(getResources().getString(R.string.redeemcode));
        mEditText.requestFocus();

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (mEditText.getText().toString().length() > 0) {
                            dialogBox.dismiss();
                            /**
                             *   redeem code service call
                             */
                            String redeemCode = mEditText.getText().toString().trim();

                            JoinTeamRestCall joinTeamRestCall = new JoinTeamRestCall(MenuActivity.this);
                            joinTeamRestCall.delegate = MenuActivity.this;
                            joinTeamRestCall.callJoinTeam(redeemCode);

                        } else if (mEditText.getText().toString().length() == 0) {
                            mEditText.setError("Please enter the Redeem Code");

                        }
                    }
                })

                .setNegativeButton(getResources().getString(R.string.skip),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                                if (isDestinations) {
                                    FragmentTransaction destinationTransaction = getSupportFragmentManager().beginTransaction();
                                    destinationTransaction.replace(R.id.FrameLayout_content, InitialFragment.newInstance());
                                    destinationTransaction.commit();
                                }
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    @Override
    public void joinTeamFinish(String message) {
        //GeneralUtils.createAlertDialog(MenuActivity.this, message);
        Toast.makeText(MenuActivity.this, message, Toast.LENGTH_SHORT).show();

        FragmentTransaction destinationTransaction = getSupportFragmentManager().beginTransaction();
        destinationTransaction.replace(R.id.FrameLayout_content, InitialFragment.newInstance());
        destinationTransaction.commit();
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
        if (userTypeID != CORPORATE_ADMIN)
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
                        .enableAutoManage(this, 34992, this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
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
        Utils.createLocationServiceChecker(googleApiClient, MenuActivity.this);
        if (!(googleApiClient.isConnected() || googleApiClient.isConnecting())) {
            googleApiClient.connect();
        } else {
            Log.d("jomy", "Client is connected");
            if (locationReport) {
                Location lastKnownLoc = LocationServices.FusedLocationApi
                        .getLastLocation(googleApiClient);
                if (lastKnownLoc != null) {
                    ReportMyLocation(lastKnownLoc);
                    locationReport = false;
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_loc_avail), Toast.LENGTH_SHORT).show();
                }
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
        locationSyncData.setReportedDate(TimeSettings.DateTimeInUTC());
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
                if (response.body() != null) {
                    if (response.isSuccess() && response.body().getErrorCode() == 0) {
                        Toast.makeText(MenuActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MenuActivity.this, getResources().getString(R.string.report_loc_fail), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast toast = Toast.makeText(MenuActivity.this, getString(R.string.data_error), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                if (NetworkChangeReceiver.isNetworkAvailable(MenuActivity.this))
                    Toast.makeText(MenuActivity.this, getResources().getString(R.string.report_loc_fail), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MenuActivity.this, getResources().getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
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

                if (response.body() != null) {

                    if (response.isSuccess() & response.body().getErrorCode() == 0) {
                        Toast.makeText(MenuActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MenuActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast toast = Toast.makeText(MenuActivity.this, getString(R.string.data_error), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
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
        if (userTypeID != CORPORATE_ADMIN)
            stopTracking();
        DataHelper dataHelper = DataHelper.getInstance(this);
        dataHelper.deleteAllLocations();
        dataHelper.deleteCheckinList();

        GeneralUtils.removeSharedPreference(MenuActivity.this, AppStringConstants.ADMIN_ID);
        GeneralUtils.removeSharedPreference(MenuActivity.this, AppStringConstants.USERID);
        GeneralUtils.removeSharedPreference(MenuActivity.this, AppStringConstants.USER_TYPE);
        GeneralUtils.removeSharedPreference(MenuActivity.this, AppStringConstants.USER_TYPE_ID);
        Intent intentLogout = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intentLogout);
        finish();
    }

}
