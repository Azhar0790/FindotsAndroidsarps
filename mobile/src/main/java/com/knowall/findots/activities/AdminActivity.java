package com.knowall.findots.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.knowall.findots.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by parijathar on 8/16/2016.
 */
public class AdminActivity extends AppCompatActivity {

    @Bind(R.id.webViewAdminPanel)
    WebView webViewAdminPanel;

    int goBackCount = 0;

    // Test Server URL
//    private static final String adminLoginUrl = "http://182.73.82.185/FindotsWeb/login/login";

    // Production Server URL
    private static final String adminLoginUrl = "https://findots.com/Login/Login";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view);

        ButterKnife.bind(this);

        webViewAdminPanel.getSettings().setJavaScriptEnabled(true);
        webViewAdminPanel.setWebViewClient(new LoadAdminPanel());
        webViewAdminPanel.setWebChromeClient(new WebChromeClient());
        webViewAdminPanel.loadUrl(adminLoginUrl);
    }


    @Override
    public void onBackPressed() {
        Log.i("Admin", webViewAdminPanel.copyBackForwardList().getCurrentIndex() + " " + goBackCount);
        if (webViewAdminPanel.copyBackForwardList().getCurrentIndex() >= 1 && goBackCount < 2) {
            webViewAdminPanel.goBack();
            goBackCount++;
        } else {
            exitConfirmation();
        }
    }

    public void exitConfirmation() {
        AlertDialog exitConfirmationDialog = new AlertDialog.Builder(AdminActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        exitConfirmationDialog.show();

    }

    class LoadAdminPanel extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(url.startsWith(WebView.SCHEME_MAILTO)) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return false;
            } else {
                view.loadUrl(url);
                return true;
            }

        }
    }

}
