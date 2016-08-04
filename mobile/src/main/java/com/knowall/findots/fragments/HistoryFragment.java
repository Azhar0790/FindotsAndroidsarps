package com.knowall.findots.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knowall.findots.R;
import com.knowall.findots.activities.MenuActivity;
import com.knowall.findots.adapters.HistoryAdapter;
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.restcalls.history.HistoryData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

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
}
