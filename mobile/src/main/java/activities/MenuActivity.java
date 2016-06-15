package activities;

import android.Manifest;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import adapters.MenuItemsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.R;
import fragments.DestinationFragment;
import interfaces.IMenuItems;

public class MenuActivity extends RuntimePermissionActivity implements IMenuItems{

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
}
