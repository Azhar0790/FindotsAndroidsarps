package com.knowall.findots.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowall.findots.R;
import com.knowall.findots.restcalls.history.HistoryData;
import com.knowall.findots.utils.timeUtils.TimeSettings;

import java.util.ArrayList;

/**
 * Created by parijathar on 7/27/2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context = null;
    HistoryData[] historyDatas = null;

    public HistoryAdapter(Context context, HistoryData[] historyDatas) {
        this.context = context;
        this.historyDatas = historyDatas;
    }

    @Override
    public int getItemCount() {
        return historyDatas.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_inflater, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        boolean isCheckIn = historyDatas[position].isCheckIn();
        boolean isCheckOut = historyDatas[position].isCheckOut();
        String destinationName = historyDatas[position].getDestinationName();
        String address = historyDatas[position].getAddress();

        holder.textViewCheckInStoreName.setText(destinationName);
        holder.textViewCheckInStoreAddress.setText(address);
        String checkInTime = TimeSettings.getTimeOnly(historyDatas[position].getCheckInDate());
        holder.buttonCheckInTime.setText(checkInTime);

        if (!isCheckOut) {
            holder.relativeLayoutCheckOut.setVisibility(View.GONE);
            holder.textViewTimeTaken.setVisibility(View.GONE);
        } else {
            holder.relativeLayoutCheckOut.setVisibility(View.VISIBLE);
            holder.textViewTimeTaken.setVisibility(View.VISIBLE);

            holder.textViewCheckOutStoreName.setText(destinationName);
            holder.textViewCheckOutStoreAddress.setText(address);
            String checkOutTime = TimeSettings.getTimeOnly(historyDatas[position].getCheckOutDate());
            holder.buttonCheckOutTime.setText(checkOutTime);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewCheckInStoreName = null;
        TextView textViewCheckInStoreAddress = null;
        Button buttonCheckInTime = null;

        RelativeLayout relativeLayoutCheckOut = null;
        TextView textViewTimeTaken = null;

        TextView textViewCheckOutStoreName = null;
        TextView textViewCheckOutStoreAddress = null;
        Button buttonCheckOutTime = null;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            textViewCheckInStoreName = (TextView) view.findViewById(R.id.textViewCheckInStoreName);
            textViewCheckInStoreAddress = (TextView) view.findViewById(R.id.textViewCheckInStoreAddress);
            buttonCheckInTime = (Button) view.findViewById(R.id.buttonCheckInTime);
            relativeLayoutCheckOut = (RelativeLayout) view.findViewById(R.id.relativeLayoutCheckOut);
            textViewTimeTaken = (TextView) view.findViewById(R.id.textViewTimeTaken);
            textViewCheckOutStoreName = (TextView) view.findViewById(R.id.textViewCheckOutStoreName);
            textViewCheckOutStoreAddress = (TextView) view.findViewById(R.id.textViewCheckOutStoreAddress);
            buttonCheckOutTime = (Button) view.findViewById(R.id.buttonCheckOutTime);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
        }
    }



}
