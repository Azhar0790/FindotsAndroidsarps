package com.knowall.findots.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.knowall.findots.R;
import com.knowall.findots.activities.MenuActivity;
import com.knowall.findots.adapters.HistoryAdapter;
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.restcalls.history.HistoryModel;
import com.knowall.findots.restcalls.history.HistoryRestCall;
import com.knowall.findots.restcalls.history.IHistory;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by parijathar on 7/27/2016.
 */
public class HistoryFragment extends Fragment implements IHistory{

    @Bind(R.id.recyclerViewHistories)
    RecyclerView recyclerViewHistories;

    @Bind(R.id.textViewNoHistory)
    TextView textViewNoHistory;

    LinearLayoutManager layoutManager = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.history, null);

        ButterKnife.bind(this, rootView);

        layoutManager = new LinearLayoutManager(MenuActivity.ContextMenuActivity);
        recyclerViewHistories.setLayoutManager(layoutManager);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        return rootView;
    }

    @Override
    public void onHistoryFailure(String errorMessage) {
        Toast.makeText(
                MenuActivity.ContextMenuActivity, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void callHistoryRestCall(String startDate, String endDate) {
        HistoryRestCall historyRestCall = new HistoryRestCall(MenuActivity.ContextMenuActivity);
        historyRestCall.delegate = HistoryFragment.this;
        historyRestCall.callGetReport(startDate, endDate);
    }

    @Override
    public void onHistorySuccess(HistoryModel historyModel) {

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        if (historyModel.getErrorCode() == 0) {
            if (historyModel.getHistoryData().length == 0) {
                textViewNoHistory.setVisibility(View.VISIBLE);
                recyclerViewHistories.setVisibility(View.GONE);
            } else {
                textViewNoHistory.setVisibility(View.GONE);
                recyclerViewHistories.setVisibility(View.VISIBLE);

                HistoryAdapter historyAdapter = new HistoryAdapter(
                        MenuActivity.ContextMenuActivity, historyModel.getHistoryData());
                recyclerViewHistories.setAdapter(historyAdapter);
                historyAdapter.notifyDataSetChanged();
            }
        } else {
            if (historyModel.getHistoryData().length == 0) {
                textViewNoHistory.setVisibility(View.VISIBLE);
                recyclerViewHistories.setVisibility(View.GONE);
            }
        }
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
                EventBus.getDefault().cancelEventDelivery(events);
                EventBus.getDefault().unregister(this);

                String startDate = "", endDate = "";

                String date = DestinationsTabFragment.current_selected_dateTime;
                if (date.equals("")) {
                    callHistoryRestCall(startDate, endDate);
                } else {
                    startDate = date.substring(0, 10)+" "+"00:00:00";
                    endDate = date.substring(0, 10)+" "+"23:59:59";
                    callHistoryRestCall(startDate, endDate);
                }

                break;

            case NOHISTORY:
                EventBus.getDefault().cancelEventDelivery(events);
                EventBus.getDefault().unregister(this);

                textViewNoHistory.setVisibility(View.VISIBLE);
                recyclerViewHistories.setVisibility(View.GONE);

                if (!EventBus.getDefault().isRegistered(this))
                    EventBus.getDefault().register(this);
                break;
        }
    }
}
