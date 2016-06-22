package activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import findots.bridgetree.com.findots.R;

/**
 * Created by parijathar on 6/21/2016.
 */
public class DetailDestinationActivity extends AppCompatActivity implements OnMapReadyCallback{

    @Bind(R.id.TextView_map_km)
    TextView mTextView_map_km;

    @Bind(R.id.TextView_address)
    TextView mTextView_address;

    @Bind(R.id.LinearLayout_checkIncheckOut)
    LinearLayout mLinearLayout_checkIncheckOut;

    @Bind(R.id.Button_checkIncheckOut)
    Button mButton_checkIncheckOut;

    @Bind(R.id.TextView_heading)
    TextView mTextView_heading;

    Toolbar mToolbar = null;
    GoogleMap googleMap = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_destination);

        ButterKnife.bind(this);

        actionBarSettings();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        mTextView_address.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng latLng = new LatLng(34, 0);
        this.googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void actionBarSettings() {

        /* Assigning the toolbar object ot the view
         * and setting the the Action bar to our toolbar
         */
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typefaceMyriadHebrew = Typeface.createFromAsset(getAssets(), "fonts/MyriadHebrew-Bold.otf");

        mTextView_heading.setText(getString(R.string.destinations));
        mTextView_heading.setTypeface(typefaceMyriadHebrew);
    }
}
