package com.knowall.findots.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

    public Serach_UserListAdapter(Context context, ArrayList<GetUserData> models) {
        mInflater = LayoutInflater.from(context);
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
    }

    public GetUserData removeItem(int position) {
        final GetUserData model = mModels.get(position);
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