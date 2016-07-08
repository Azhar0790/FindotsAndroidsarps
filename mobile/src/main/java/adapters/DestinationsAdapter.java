package adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;

import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.R;
import interfaces.IDestinations;
import restcalls.destinations.DestinationData;
import utils.GeneralUtils;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.ViewHolder>{

    public static IDestinations delegate = null;

    Context context = null;
    ArrayList<DestinationData> destinationDatas = null;
    final String assignedBy = "Assigned by ";
    String getTime = null;

    public DestinationsAdapter(Context context, ArrayList<DestinationData> destinationDatas) {
        this.context = context;
        this.destinationDatas = destinationDatas;

        DateTimeFormatter fmt1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
        DateTime dateTime = new DateTime();
        getTime = dateTime.toString(fmt1);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTextView_destinationAssignedBy = null;
        TextView mTextView_destinationName = null;
        LinearLayout mLinearLayout_checkIncheckOut = null;
        Button mButton_checkIncheckOut = null;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            mTextView_destinationAssignedBy = (TextView) view.findViewById(R.id.TextView_destinationAssignedBy);
            mTextView_destinationName = (TextView) view.findViewById(R.id.TextView_destinationName);
            mLinearLayout_checkIncheckOut = (LinearLayout) view.findViewById(R.id.LinearLayout_checkIncheckOut);
            mButton_checkIncheckOut = (Button) view.findViewById(R.id.Button_checkIncheckOut);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            delegate.onDestinationSelected(adapterPosition);
        }
    }

    @Override
    public int getItemCount() {
        return destinationDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.destinations_inflater, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        /**
         *   to display assigned Destination Time, get the time difference
         */
        String assignDestinationTime = destinationDatas.get(position).getAssigndestinationTime();
        if (assignDestinationTime.length() != 0) {
            String timeDifference = dateTimeDifference(assignDestinationTime);
            holder.mTextView_destinationAssignedBy.setText(assignedBy + destinationDatas.get(position).getName() +" "+timeDifference);
        } else {
            holder.mTextView_destinationAssignedBy.setText(assignedBy + destinationDatas.get(position).getName());
        }

        /**
         *   set drawable left icon and setting the size
         */
        holder.mTextView_destinationAssignedBy.setCompoundDrawables(
                GeneralUtils.scaleDrawable(this.context.getResources().getDrawable(R.drawable.destination_timer), 28, 28),
                null, null, null);

        /**
         *   set Destination Name
         */
        holder.mTextView_destinationName.setText(destinationDatas.get(position).getDestinationName());

        /**
         *   based on CheckIn / CheckOut status
         *   display the background and button name
         *   set checked_out time if any.
         */
        boolean isCheckIn = destinationDatas.get(position).isCheckedIn();
        boolean isCheckOut = destinationDatas.get(position).isCheckedOut();

        if (!isCheckIn) {
            holder.mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checkin);
            holder.mLinearLayout_checkIncheckOut.setEnabled(true);
            holder.mButton_checkIncheckOut.setEnabled(true);
            holder.mButton_checkIncheckOut.setText(context.getString(R.string.checkin));
            holder.mButton_checkIncheckOut.setTextColor(context.getResources().getColor(R.color.green));
        } else if (!isCheckOut) {
            holder.mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checkout);
            holder.mLinearLayout_checkIncheckOut.setEnabled(true);
            holder.mButton_checkIncheckOut.setEnabled(true);
            holder.mButton_checkIncheckOut.setText(context.getString(R.string.checkout));
            holder.mButton_checkIncheckOut.setTextColor(context.getResources().getColor(R.color.app_color));
        } else {
            String checkedOutTime = destinationDatas.get(position).getCheckedOutReportedDate();
            holder.mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checked_at);
            holder.mLinearLayout_checkIncheckOut.setEnabled(false);
            holder.mButton_checkIncheckOut.setEnabled(false);
            holder.mButton_checkIncheckOut.setText(context.getString(R.string.checkedout_at)+" "+ GeneralUtils.getCheckedOutTime(checkedOutTime));
            holder.mButton_checkIncheckOut.setCompoundDrawables(
                    GeneralUtils.scaleDrawable(this.context.getResources().getDrawable(R.drawable.checkedout_tick), 40, 40),
                    null, null, null);
            holder.mButton_checkIncheckOut.setTextColor(Color.WHITE);
        }

        /**
         *   on clicking below LinearLayout or Button , call CheckInCheckOut API
         */
        holder.mLinearLayout_checkIncheckOut.setTag(position);
        holder.mLinearLayout_checkIncheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int destinationPosition = (int) v.getTag();
                delegate.callCheckInCheckOutService(destinationPosition, destinationDatas.get(position).isCheckedIn());
            }
        });

        holder.mButton_checkIncheckOut.setTag(position);
        holder.mButton_checkIncheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int destinationPosition = (int) v.getTag();
                delegate.callCheckInCheckOutService(destinationPosition, destinationDatas.get(position).isCheckedIn());
            }
        });
    }


    /**
     *   get Time difference
     * @param date
     * @return
     */
    public String dateTimeDifference(String date) {

        String timeDifference = null;

        Log.i(Constants.TAG, "dateTimeDifference: getTime = "+getTime);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
        DateTime endTime = fmt.parseDateTime(getTime);
        DateTime startTime = fmt.parseDateTime(date);

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
                timeDifference = years + " year ago";
            } else {
                timeDifference = years + " years ago";
            }
        } else if (months != 0) {
            if (months == 1) {
                timeDifference = months + " month ago";
            } else {
                timeDifference = months + " months ago";
            }
        } else if (weeks != 0) {
            if (weeks == 1) {
                timeDifference = weeks + " week ago";
            } else {
                timeDifference = weeks + " weeks ago";
            }
        } else if(days != 0) {
            if (days == 1) {
                timeDifference = days + " day ago";
            } else {
                timeDifference = days + " days ago";
            }
        } else if (hours != 0) {
            if (hours == 1) {
                timeDifference = hours + " hr ago";
            } else {
                timeDifference = hours + " hrs ago";
            }
        } else if (minutes != 0) {
            if (minutes == 1) {
                timeDifference = minutes + " min ago";
            } else {
                timeDifference = minutes + " mins ago";
            }
        } else if (seconds != 0 ){
            if (seconds == 1) {
                timeDifference = "few secs ago";
            } else {
                timeDifference = "few secs ago";
            }
        }

        return timeDifference;
    }
}
