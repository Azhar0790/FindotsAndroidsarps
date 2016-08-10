package com.knowall.findots.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.knowall.findots.R;
public class UserListViewHolder extends RecyclerView.ViewHolder {

    public TextView userNameText;
    public ImageView current_user_indicator;

    public UserListViewHolder(View itemView) {
        super(itemView);

        userNameText = (TextView) itemView.findViewById(R.id.userName);
        current_user_indicator = (ImageView) itemView.findViewById(R.id.current_user_indicator);
    }


}
