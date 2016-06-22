package adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import findots.bridgetree.com.findots.R;
import interfaces.IDestinations;
import restcalls.destinations.DestinationData;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.ViewHolder>{

    public static IDestinations delegate = null;

    Context context = null;
    DestinationData[] destinationDatas = null;
    final String assignedBy = "Assigned by ";

    public DestinationsAdapter(Context context, DestinationData[] destinationDatas) {
        this.context = context;
        this.destinationDatas = destinationDatas;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context = null;

        TextView mTextView_destinationAssignedBy = null;
        TextView mTextView_destinationName = null;
        LinearLayout mLinearLayout_checkIncheckOut = null;
        Button mButton_checkIncheckOut = null;

        public ViewHolder(Context context, View view) {
            super(view);
            view.setOnClickListener(this);
            this.context = context;

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
        return destinationDatas.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.destinations_inflater, null);
        ViewHolder viewHolder = new ViewHolder(context, v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView_destinationAssignedBy.setCompoundDrawables(
                scaleDrawable(this.context.getResources().getDrawable(R.drawable.destination_timer), 28, 28),
                null, null, null);

        holder.mTextView_destinationAssignedBy.setText(assignedBy + destinationDatas[position].getName());
        holder.mTextView_destinationName.setText(destinationDatas[position].getDestinationName());

        boolean isCheckIn = destinationDatas[position].getCheckedIn();
        boolean isCheckOut = destinationDatas[position].getCheckedOut();

        if (!isCheckIn) {
            holder.mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checkin);
            holder.mButton_checkIncheckOut.setText(context.getString(R.string.checkin));
            holder.mButton_checkIncheckOut.setTextColor(context.getResources().getColor(R.color.green));
        } else if (!isCheckOut) {
            holder.mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checkout);
            holder.mButton_checkIncheckOut.setText(context.getString(R.string.checkout));
            holder.mButton_checkIncheckOut.setTextColor(context.getResources().getColor(R.color.app_color));
        } else {
            holder.mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checked_at);
            holder.mButton_checkIncheckOut.setText(context.getString(R.string.checkedout_at)+" 5.15pm");
            holder.mButton_checkIncheckOut.setCompoundDrawables(
                    scaleDrawable(this.context.getResources().getDrawable(R.drawable.checkedout_tick), 40, 40),
                    null, null, null);
            holder.mButton_checkIncheckOut.setTextColor(Color.WHITE);
        }
    }


    public Drawable scaleDrawable(Drawable drawable, int width, int height) {

        int wi = drawable.getIntrinsicWidth();
        int hi = drawable.getIntrinsicHeight();
        int dimDiff = Math.abs(wi - width) - Math.abs(hi - height);
        float scale = (dimDiff > 0) ? width / (float)wi : height /
                (float)hi;
        Rect bounds = new Rect(0, 0, (int)(scale * wi), (int)(scale * hi));
        drawable.setBounds(bounds);
        return drawable;
    }


}
