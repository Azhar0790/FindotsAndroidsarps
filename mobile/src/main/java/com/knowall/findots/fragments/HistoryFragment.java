package com.knowall.findots.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.activities.MenuActivity;
import com.knowall.findots.activities.ViewHistoryFileActivity;
import com.knowall.findots.adapters.HistoryAdapter;
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.restcalls.history.HistoryData;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.google.android.gms.internal.zznu.io;
import static com.knowall.findots.activities.SplashActivity.DELAY_TIME;

/**
 * Created by parijathar on 7/27/2016.
 */
public class HistoryFragment extends Fragment {

    @Bind(R.id.recyclerViewHistories)
    RecyclerView recyclerViewHistories;

    @Bind(R.id.textViewNoHistory)
    TextView textViewNoHistory;


    ViewGroup rootView = null;
    LayoutInflater inflater = null;
    ArrayList<HistoryData> historyDataList=new ArrayList<HistoryData>();
    LinearLayoutManager layoutManager;
    HistoryAdapter historyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d("paul", "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = (ViewGroup) inflater.inflate(R.layout.history, null);
        ButterKnife.bind(this, rootView);
        layoutManager = new LinearLayoutManager(MenuActivity.ContextMenuActivity);
        recyclerViewHistories.setLayoutManager(layoutManager);
        setHistoryAdapter(historyDataList);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Log.d("paul", "onCreateView");

        FinDotsApplication.getInstance().trackScreenView("History Screen");

        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.floatingbtn_generate_pdf);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "clicked txtviw", Toast.LENGTH_SHORT).show();

                //generate_bitmap();
                //create_pdf(recyclerViewHistories);
                new GeneratePdfTask().execute();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*CalendarDay selectedDay = DestinationsTabFragment.materialCalendarView.getSelectedDate();

        if (selectedDay.isAfter(CalendarDay.today())) {
            textViewNoHistory.setVisibility(View.VISIBLE);
            recyclerViewHistories.setVisibility(View.GONE);
        } else {
            String startDate = "", endDate = "";

            String date = DestinationsTabFragment.current_selected_dateTime;
            if (date.equals("")) {
                callHistoryRestCall(startDate, endDate);
            } else {
                startDate = date.substring(0, 10) + " " + "00:00:00";
                endDate = date.substring(0, 10) + " " + "23:59:59";
                callHistoryRestCall(startDate, endDate);
            }
        }*/
    }

    public void setHistoryAdapter( ArrayList<HistoryData> historyDataList) {

        historyAdapter = new HistoryAdapter(
                getActivity(), historyDataList);
        recyclerViewHistories.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    public void onEvent(AppEvents events) {
        switch (events) {
            case HISTORY:
//                EventBus.getDefault().cancelEventDelivery(events);
//                EventBus.getDefault().unregister(this);
                textViewNoHistory.setVisibility(View.GONE);
                recyclerViewHistories.setVisibility(View.VISIBLE);
//              recyclerViewHistories.removeAllViews();
                Log.d("paul","historySize..."+DestinationsTabFragment.historyDatas.size());
                historyDataList.clear();
                historyDataList.addAll(DestinationsTabFragment.historyDatas);
//                HistoryAdapter historyAdapter = new HistoryAdapter(
//                MenuActivity.ContextMenuActivity, historyDataList);
//                recyclerViewHistories.setLayoutManager(new LinearLayoutManager(MenuActivity.ContextMenuActivity));
//                recyclerViewHistories.setAdapter(historyAdapter);
//                historyAdapter.notifyDataSetChanged();
                setHistoryAdapter(historyDataList);
                if (!EventBus.getDefault().isRegistered(this))
                    EventBus.getDefault().register(this);
                break;

            case NOHISTORY:
                Log.d("paul","history44...");
                textViewNoHistory.setVisibility(View.VISIBLE);
                recyclerViewHistories.setVisibility(View.GONE);
//                EventBus.getDefault().cancelEventDelivery(events);
//                EventBus.getDefault().unregister(this);
                Log.d("paul","history44Size ..."+historyDataList.size());
//                recyclerViewHistories.removeAllViews();
                historyDataList.clear();
                DestinationsTabFragment.historyDatas.clear();
                setHistoryAdapter(historyDataList);
                Log.d("paul","history44Size2 ..."+historyDataList.size()+" : "+DestinationsTabFragment.historyDatas.size());


                if (!EventBus.getDefault().isRegistered(this))
                    EventBus.getDefault().register(this);
                break;
        }
    }


/*
    public Bitmap getWholeListViewItemsToBitmap() {

        int itemscount       = layoutManager.getChildCount();
        int allitemsheight   = 0;
        List<Bitmap> bmps    = new ArrayList<Bitmap>();


        View view = layoutManager.findViewByPosition(0);

        if (view == null)
            Toast.makeText(getActivity(), "null view", Toast.LENGTH_SHORT).show();

        for (int i = 0; i < itemscount; i++) {

            View childView = layoutManager.findViewByPosition(i);
            */
/*childView.measure(View.MeasureSpec.makeMeasureSpec(recyclerViewHistories.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));*//*


            childView.measure(childView.getWidth(), childView.getHeight());

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight+=childView.getMeasuredHeight();
        }

        Bitmap bigbitmap    = Bitmap.createBitmap(recyclerViewHistories.getMeasuredWidth(), allitemsheight, Bitmap.Config.RGB_565);
        Canvas bigcanvas    = new Canvas(bigbitmap);

        Paint paint = new Paint();
        int iHeight = 0;

        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight+=bmp.getHeight();

            bmp.recycle();
            bmp=null;
        }


        return bigbitmap;
    }


    File imgFile = null;
    public void generate_bitmap() {
        Bitmap b = getWholeListViewItemsToBitmap();
        String fileStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        OutputStream outputStream = null;
        try{
            imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"findots1_"+fileStamp+".png");
            Log.d("IMG_PATH",imgFile.getAbsolutePath());
            outputStream = new FileOutputStream(imgFile);
            b.compress(Bitmap.CompressFormat.PNG,40,outputStream);
            outputStream.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



    public void generate_pdf() {

        //String fileStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //File imgFile = new File("mnt/sdcard/history10"+"_"+fileStamp+".pdf");

        //Bitmap screen = getWholeListViewItemsToBitmap();
        // Saving screenshot to file
        //Falcon.takeScreenshot(getActivity(), imgFile);
    // Take bitmap and do whatever you want
        Bitmap bitmap = getWholeListViewItemsToBitmap();


        String fileStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String file = "mnt/sdcard/history10"+"_"+fileStamp+".pdf";

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            add_image(document, bytes);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void add_image(Document document, byte[] bytes) {
        Image image = null;
        try {
            image = Image.getInstance(bytes);
        } catch (BadElementException be) {
            be.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            document.add(image);
        } catch (DocumentException de) {
            de.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
*/


    LruCache<String, Bitmap> bitmaCache = null;
    int size = 0 ;
    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
//                holder.itemView.setDrawingCacheEnabled(false);
//                holder.itemView.destroyDrawingCache();
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }

        }
        return bigBitmap;
    }


    /*public void createBitmap(RecyclerView view) {

        Bitmap bm = getScreenshotFromRecyclerView(view);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/req_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create_pdf(myDir, file.getAbsolutePath());

    }*/

    public String create_pdf(RecyclerView view) {

        Bitmap bm = getScreenshotFromRecyclerView(view);

        try {
            /**
             *   create application folder
             */
            File folder = new File(Environment.getExternalStorageDirectory(), "Findots");

            if (!folder.exists())
                folder.mkdir();

            /**
             *  create history file to save bitmap
             */
            String fileStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String history_pdf = "history_" + fileStamp + ".pdf";
            File pdf_file = new File(folder, history_pdf);

            if (!pdf_file.exists()) {
                pdf_file.createNewFile();
            }

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdf_file.getAbsoluteFile()));
            document.open();

            /*String username = GeneralUtils.getSharedPreferenceString(getActivity(), AppStringConstants.NAME);

            Font mainHeadingFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
            Paragraph paragraph = new Paragraph(username+" "+fileStamp, mainHeadingFont);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);*/

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100 , stream);
            byte[] bytes = stream.toByteArray();
            Image myImg = Image.getInstance(bytes);

            //myImg.scaleToFit(PageSize.A4.getHeight(), PageSize.A4.getWidth());
            myImg.scaleToFit(595, 842);
            //myImg.setAbsolutePosition(0, 0);
            myImg.setAlignment(Image.ALIGN_CENTER);
            document.add(myImg);
            document.newPage();

            document.close();

            return pdf_file.getAbsolutePath();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    class GeneratePdfTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            GeneralUtils.initialize_progressbar(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {
            String filepath = create_pdf(recyclerViewHistories);
            return filepath;
        }

        @Override
        protected void onPostExecute(String s) {

            GeneralUtils.stop_progressbar();

            if (s != null) {
                Bundle bundle = new Bundle();
                bundle.putString("filepath", s);

                Intent intent = new Intent(getActivity(), ViewHistoryFileActivity.class);
                intent.putExtras(bundle);
                //startActivity(intent);
                startActivityForResult(intent, GeneratePdfCode);
            } else {
                Toast.makeText(getActivity(), "Error in downloading pdf", Toast.LENGTH_SHORT).show();
            }
        }
    }


    final int GeneratePdfCode = 11;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GeneratePdfCode) {
            /**
             *  delete the file
             */


            File folder = new File(Environment.getExternalStorageDirectory(), "Findots");

            if (folder.exists()) {

                for (File file: folder.listFiles())
                    file.delete();

                folder.delete();
            }

        }
    }
}
