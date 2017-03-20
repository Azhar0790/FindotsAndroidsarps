package com.knowall.findots.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.knowall.findots.R;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by parijathar on 3/15/2017.
 */

public class ViewHistoryFileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_history_file_activity);

        String username = GeneralUtils.getSharedPreferenceString(this, AppStringConstants.NAME);
        String fileStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        TextView mTextViewHeading = (TextView) findViewById(R.id.TextView_heading);
        mTextViewHeading.setText(username+" "+fileStamp);


        ImageView mImageViewShare = (ImageView) findViewById(R.id.imageViewDirections);
        mImageViewShare.setVisibility(View.VISIBLE);
        mImageViewShare.setImageResource(R.drawable.ic_share);

        Bundle b = getIntent().getExtras();
        String filepath = null;

        if (b != null) {
            filepath = b.getString("filepath");
        }

        /*WebView mWebViewHistoryFile = (WebView) findViewById(R.id.webview_history_file);
        *//*mWebViewHistoryFile.getSettings().setJavaScriptEnabled(true);
        mWebViewHistoryFile.getSettings().setLoadsImagesAutomatically(true);
        mWebViewHistoryFile.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);*//*
        mWebViewHistoryFile.getSettings().setBuiltInZoomControls(true);

        mWebViewHistoryFile.getSettings().setDisplayZoomControls(false);
        mWebViewHistoryFile.getSettings().setLoadWithOverviewMode(true);
        mWebViewHistoryFile.getSettings().setUseWideViewPort(true);
        //mWebViewHistoryFile.loadUrl("file:///"+filepath);
        mWebViewHistoryFile.loadUrl("https://docs.google.com/viewer?url="+filepath);
*/


        final File file = new File(filepath);

        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromFile(file)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();



        mImageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("text/plain");
                //intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });

    }

    final int GeneratePdfCode = 11;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(GeneratePdfCode, intent);
        finish();
    }
}
