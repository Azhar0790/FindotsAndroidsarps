package com.knowall.findots.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowall.findots.R;
import com.knowall.findots.activities.MenuActivity;
import com.knowall.findots.restcalls.history.HistoryData;
import com.knowall.findots.utils.timeUtils.TimeSettings;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by parijathar on 7/27/2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context = null;
    HistoryData[] historyDatas = null;
    Typeface typefaceLight = null;
    Typeface typefaceMedium = null;

    public HistoryAdapter(Context context, HistoryData[] historyDatas) {
        this.context = context;
        this.historyDatas = historyDatas;
        typefaceLight = Typeface.createFromAsset(MenuActivity.ContextMenuActivity.getAssets(), "fonts/Roboto-Light.ttf");
        typefaceMedium = Typeface.createFromAsset(MenuActivity.ContextMenuActivity.getAssets(), "fonts/Roboto-Medium.ttf");
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

        holder.textViewCheckInStoreAddress.setTypeface(typefaceLight);
        holder.textViewCheckInStoreName.setTypeface(typefaceMedium);
        holder.buttonCheckInTime.setTypeface(typefaceLight);
        holder.buttonCheckIN.setTypeface(typefaceLight);
        holder.buttonCheckOUT.setTypeface(typefaceLight);

        holder.textViewCheckInStoreName.setText(destinationName);
        holder.textViewCheckInStoreAddress.setText(address);

        if (isCheckIn) {
            String checkInTime = TimeSettings.getTimeOnly(historyDatas[position].getCheckInDate());
            holder.buttonCheckInTime.setText(checkInTime);
        } else {
            holder.buttonCheckIN.setBackgroundResource(R.drawable.selector_checkin_round_strike);
            holder.buttonCheckIN.setText("");
            holder.buttonCheckInTime.setText("Not Checked In");
            holder.buttonCheckInTime.setTextSize(12f);
        }

        if (!isCheckOut) {
            holder.relativeLayoutCheckOut.setVisibility(View.GONE);
            holder.textViewTimeTaken.setVisibility(View.GONE);
            holder.viewCheckInLine.setVisibility(View.GONE);
        } else {
            holder.relativeLayoutCheckOut.setVisibility(View.VISIBLE);
            holder.textViewTimeTaken.setVisibility(View.VISIBLE);
            holder.viewCheckInLine.setVisibility(View.VISIBLE);

            holder.textViewTimeTaken.setTypeface(typefaceLight);
            holder.textViewCheckOutStoreName.setTypeface(typefaceMedium);
            holder.textViewCheckOutStoreAddress.setTypeface(typefaceLight);
            holder.buttonCheckOutTime.setTypeface(typefaceLight);

            String checkInDateTime = historyDatas[position].getCheckInDate();
            String checkOutDateTime = historyDatas[position].getCheckOutDate();
            String timeTaken = getTimeTaken(checkInDateTime, checkOutDateTime);
            holder.textViewTimeTaken.setText(timeTaken);

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
        View viewCheckInLine = null;

        RelativeLayout relativeLayoutCheckOut = null;
        TextView textViewTimeTaken = null;

        TextView textViewCheckOutStoreName = null;
        TextView textViewCheckOutStoreAddress = null;
        Button buttonCheckOutTime = null;

        Button buttonCheckIN = null;
        Button buttonCheckOUT = null;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            textViewCheckInStoreName = (TextView) view.findViewById(R.id.textViewCheckInStoreName);
            textViewCheckInStoreAddress = (TextView) view.findViewById(R.id.textViewCheckInStoreAddress);
            buttonCheckInTime = (Button) view.findViewById(R.id.buttonCheckInTime);
            viewCheckInLine = (View) view.findViewById(R.id.viewCheckInLine);
            relativeLayoutCheckOut = (RelativeLayout) view.findViewById(R.id.relativeLayoutCheckOut);
            textViewTimeTaken = (TextView) view.findViewById(R.id.textViewTimeTaken);
            textViewCheckOutStoreName = (TextView) view.findViewById(R.id.textViewCheckOutStoreName);
            textViewCheckOutStoreAddress = (TextView) view.findViewById(R.id.textViewCheckOutStoreAddress);
            buttonCheckOutTime = (Button) view.findViewById(R.id.buttonCheckOutTime);
            buttonCheckIN = (Button) view.findViewById(R.id.buttonCheckIN);
            buttonCheckOUT = (Button) view.findViewById(R.id.buttonCheckOUT);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
        }
    }


    private String getTimeTaken(String checkInDate, String checkOutDate) {

        String timeDifference = null;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime startTime = fmt.parseDateTime(checkInDate);
        DateTime endTime = fmt.parseDateTime(checkOutDate);

        Period period = new Period(startTime, endTime);

        int years = period.getYears();
        int months = period.getMonths();
        int weeks = period.getWeeks();
        int days = period.getDays();
        int hours = period.getHours();
        int minutes = period.getMinutes();
        int seconds = period.getSeconds();

        if (years != 0) {
            if (years == 1) {
                timeDifference = years + " year";
            } else {
                timeDifference = years + " years";
            }
        } else if (months != 0) {
            if (months == 1) {
                timeDifference = months + " month";
            } else {
                timeDifference = months + " months";
            }
        } else if (weeks != 0) {
            if (weeks == 1) {
                timeDifference = weeks + " week";
            } else {
                timeDifference = weeks + " weeks";
            }
        } else if (days != 0) {
            if (days == 1) {
                timeDifference = days + " day";
            } else {
                timeDifference = days + " days";
            }
        } else if (hours != 0) {
            if (hours == 1) {
                timeDifference = hours + " hr";
            } else {
                timeDifference = hours + " hrs";
            }
        } else if (minutes != 0) {
            if (minutes == 1) {
                timeDifference = minutes + " min";
            } else {
                timeDifference = minutes + " mins";
            }
        } else if (seconds != 0) {
            if (seconds == 1) {
                timeDifference = seconds +" sec";
            } else {
                timeDifference = seconds + " secs";
            }
        }

        return timeDifference;
    }
}
