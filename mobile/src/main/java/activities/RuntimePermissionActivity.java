package activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;

import findots.bridgetree.com.findots.R;

/**
 * Created by parijathar on 6/8/2016.
 */
public abstract class RuntimePermissionActivity extends AppCompatActivity {

    private SparseIntArray mErrorString = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mErrorString = new SparseIntArray();
    }

    public void requestAppPermissions(final String[] requestedPermissions, final int stringID,
                                      final int requestCode) {

        mErrorString.put(requestCode, stringID);

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean shouldShowRequestPermissionRationale = false;

        for (String permission: requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        stringID,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.grant), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(
                                        RuntimePermissionActivity.this,
                                        requestedPermissions,
                                        requestCode);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(
                        RuntimePermissionActivity.this,
                        requestedPermissions,
                        requestCode);
            }
        } else {
            onPermissionsGranted(requestCode);
        }

    }

    public abstract void onPermissionsGranted(int requestCode);

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission: grantResults) {
            permissionCheck = permissionCheck + permission;
        }

        if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode);
        } else {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    mErrorString.get(requestCode),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.enable), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:"+getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent);

                        }
                    }).show();
        }
    }
}
