package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import findots.bridgetree.com.findots.R;
import interfaces.IDestinations;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.ViewHolder>{

    public static IDestinations delegate = null;

    Context context = null;
    ArrayList<String> arrayList = null;

    public DestinationsAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
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
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.destinations_inflater, parent, false);
        ViewHolder viewHolder = new ViewHolder(context, v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView_destinationName.setText(arrayList.get(position));
    }
}
