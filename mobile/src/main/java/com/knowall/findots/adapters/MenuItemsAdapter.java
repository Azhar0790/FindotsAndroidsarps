package com.knowall.findots.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.knowall.findots.R;
import com.knowall.findots.interfaces.IMenuItems;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

/**
 * Created by parijathar on 5/26/2016.
 */
public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>{

    Context mContext = null;

    /*
     * Declaring Variable to Understand which View is being worked on
     */
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    /*
     *  String Array and int Array to history titles and icons
     *  of DrawerLayout
     */
    private String TITLES[];
    private int ICONS[];

    public static IMenuItems delegate = null;

    String username;
    String email;

    Typeface typefaceLight = null;
    public static int userTypeID = 0;

    /**
     *   User Types
     */
    public static final int INDIVIDUAL = 1;
    public static final int CORPORATE = 2;
    public static final int CORPORATE_ADMIN = 3;
    public static final int WEB_USER_ADMIN = 4;
    public static final int COUNTRY_SPECIFIC_ADMIN = 5;

    /**
     *  Constructor
     */
    public MenuItemsAdapter(Context mContext, String TITLES[],
                            int ICONS[], String username, String email) {
        this.mContext = mContext;
        this.TITLES = TITLES;
        this.ICONS = ICONS;
        this.username = username;
        this.email = email;

        userTypeID = GeneralUtils.getSharedPreferenceInt(mContext, AppStringConstants.USER_TYPE_ID);
        typefaceLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        //Inflating the layout
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_row, null);
        } else if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, null);
        }

        //Creating ViewHolder and passing the object of type view
        ViewHolder viewHolderItem = new ViewHolder(mContext, view, viewType);

        return viewHolderItem;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.holderID == TYPE_HEADER) {
            /*Glide.with(mContext)
                    .load(R.drawable.userimage)
                    .transform(new CircleTransform(mContext))

                    .into(holder.mImageView_userImage);*/
            holder.mTextView_userName.setTypeface(typefaceLight);
            holder.mTextView_userName.setText(username);

        } else if (holder.holderID == TYPE_ITEM) {
            holder.mTextView_menuItem.setTypeface(typefaceLight);
            holder.mTextView_menuItem.setText(TITLES[position - 1]);
            holder.mImageView_menuItem.setBackgroundResource(ICONS[position-1]);

            if ((position - 1) == (TITLES.length - 1)) {
                holder.textViewStopLocationUpdates.setVisibility(View.VISIBLE);
                holder.textViewStopLocationUpdates.setTypeface(typefaceLight);
            } else {
                holder.textViewStopLocationUpdates.setVisibility(View.GONE);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context mContext = null;
        int holderID;

        // Header widgets
        ImageView mImageView_userImage = null;
        TextView mTextView_userName = null;

        // Menu Item widgets
        ImageView mImageView_menuItem = null;
        TextView mTextView_menuItem = null;
        TextView textViewStopLocationUpdates = null;

        public ViewHolder(Context mContext, View view, int viewType) {
            super(view);
            view.setOnClickListener(this);
            this.mContext = mContext;

            if (viewType == TYPE_HEADER) {
                holderID = TYPE_HEADER;

                mImageView_userImage = (ImageView) view.findViewById(R.id.ImageView_userImage);
                mTextView_userName = (TextView) view.findViewById(R.id.TextView_userName);

            } else if (viewType == TYPE_ITEM) {
                holderID = TYPE_ITEM;

                mImageView_menuItem = (ImageView) view.findViewById(R.id.ImageView_menuItem);
                mTextView_menuItem = (TextView) view.findViewById(R.id.TextView_menuItem);
                textViewStopLocationUpdates = (TextView) view.findViewById(R.id.textViewStopLocationUpdates);
            }

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Log.d("jomy","pos >.."+adapterPosition);

            if (MenuItemsAdapter.userTypeID == CORPORATE) {
                delegate.onMenuItemSelected(adapterPosition);
            } else if (userTypeID == CORPORATE_ADMIN) {
                if (adapterPosition > 1) {
                    delegate.onMenuItemSelected(adapterPosition + 2);
                } else {
                    delegate.onMenuItemSelected(adapterPosition);
                }
            }


        }
    }

    @Override
    public int getItemCount() {
        // the number of items in the list will be +2 the titles including
        // the header view and Logout view
        return TITLES.length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderType(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }

    }

    private boolean isHeaderType(int position) {
        return position == 0;
    }
}
