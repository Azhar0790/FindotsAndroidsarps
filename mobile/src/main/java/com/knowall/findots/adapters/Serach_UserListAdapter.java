package com.knowall.findots.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knowall.findots.R;
import com.knowall.findots.restcalls.getUser.GetUserData;

import java.util.ArrayList;

/**
 * Created by jpaulose on 8/10/2016.
 */
public class Serach_UserListAdapter extends RecyclerView.Adapter<UserListViewHolder> {

    private final LayoutInflater mInflater;
    private final ArrayList<GetUserData> mModels;
    Context mContext;
    public Serach_UserListAdapter(Context context, ArrayList<GetUserData> models) {
        mContext=context;
        mInflater = LayoutInflater.from(mContext);
        mModels = new ArrayList<GetUserData>(models);
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.search_user_item, parent, false);
        return new UserListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserListViewHolder holder, int position) {
        holder.userNameText.setText(""+mModels.get(position).getName());
        holder.userListLay.setTag(position);
        holder.userListLay.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "success");
                if(((int)v.getTag())==0)
                    returnIntent.putExtra("allUser", true);
                else
                {
                    returnIntent.putExtra("allUser", false);
                    returnIntent.putExtra("userID", ""+mModels.get(((int)v.getTag())).getUserID());
                    returnIntent.putExtra("userName", ""+mModels.get(((int)v.getTag())).getName());
                }
                ((Activity) mContext).setResult(Activity.RESULT_OK, returnIntent);
                ((Activity) mContext).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(ArrayList<GetUserData> models) {

        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(ArrayList<GetUserData> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final GetUserData model = mModels.get(i);
            if (!newModels.contains(model)) {
                Log.d("jomy", "size filterName  : "+model.getName());
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<GetUserData> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final GetUserData model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<GetUserData> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final GetUserData model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
        notifyDataSetChanged();
    }

    public GetUserData removeItem(int position) {
        final GetUserData model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, GetUserData model) {

        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final GetUserData model = mModels.get(toPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}