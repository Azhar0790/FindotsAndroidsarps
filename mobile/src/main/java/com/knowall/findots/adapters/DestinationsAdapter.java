package com.knowall.findots.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knowall.findots.R;
import com.knowall.findots.activities.DetailDestinationActivity;
import com.knowall.findots.activities.MenuActivity;
import com.knowall.findots.distancematrix.model.Elements;
import com.knowall.findots.interfaces.IDestinations;
import com.knowall.findots.restcalls.destinations.DestinationData;
import com.knowall.findots.utils.AddTextWatcher;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;
import com.knowall.findots.utils.timeUtils.TimeSettings;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.ViewHolder> {

    public static IDestinations delegate = null;

    Context context = null;
    ArrayList<DestinationData> destinationDatas = null;
    final String scheduledAt = "Scheduled at ";
    String getTime = null;
    int userID = 0;
    int userTypeID = 0;
    int assignedUserID = 0;
    private long lastClickTime = 0;
    Elements[] elements = null;
    //double currentLatitude;
    //double currentLongitude;
    Typeface typefaceLight = null;
    public static boolean show_admin_comment = false;
    public static String admin_comment = null;

    public DestinationsAdapter(Context context, ArrayList<DestinationData> destinationDatas/*, Elements[] elements*/
    ) {
        this.context = context;
        this.destinationDatas = destinationDatas;

        DateTimeFormatter fmt1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
        DateTime dateTime = new DateTime();
        getTime = dateTime.toString(fmt1);

        userID = GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID);
        userTypeID = GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USER_TYPE_ID);

        typefaceLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");

        //getCurrentLatitudeAndLongitude();

    }

    /*public void getCurrentLatitudeAndLongitude() {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            List<String> providers = locationManager.getProviders(true);
            Location location = null;

            for (String provider : providers) {
                Location currentLocation = locationManager.getLastKnownLocation(provider);
                if (currentLocation == null) {
                    continue;
                }
                if (location == null || currentLocation.getAccuracy() < location.getAccuracy()) {
                    location = currentLocation;
                }
            }

            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
            } else {
                DataHelper dataHelper = DataHelper.getInstance(context);
                List<LocationData> locationLatestData = dataHelper.getLocationLastRecord();
                if (locationLatestData.size() > 0) {
                    for (LocationData locLastData : locationLatestData) {
                        currentLatitude = locLastData.getLatitude();
                        currentLongitude = locLastData.getLongitude();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTextView_destinationAssignedBy = null;
        TextView mTextView_destinationName = null;
        LinearLayout mLinearLayout_checkIncheckOut = null;
        Button mButton_checkIncheckOut = null;
        TextView textViewTravelTime = null;
        TextView textViewSchedule = null;
        TextView mTextView_destinationScheduled = null;
        ImageView mCheckin_notes = null;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            mTextView_destinationAssignedBy = (TextView) view.findViewById(R.id.TextView_destinationAssignedBy);
            mTextView_destinationName = (TextView) view.findViewById(R.id.TextView_destinationName);
            mLinearLayout_checkIncheckOut = (LinearLayout) view.findViewById(R.id.LinearLayout_checkIncheckOut);
            mButton_checkIncheckOut = (Button) view.findViewById(R.id.Button_checkIncheckOut);
            textViewTravelTime = (TextView) view.findViewById(R.id.textViewTravelTime);
            textViewSchedule = (TextView) view.findViewById(R.id.textViewSchedule);
            mTextView_destinationScheduled = (TextView) view.findViewById(R.id.TextView_destinationScheduled);
            mCheckin_notes = (ImageView) view.findViewById(R.id.checkin_notes);
        }

        @Override
        public void onClick(View v) {

            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return;
            }

            lastClickTime = SystemClock.elapsedRealtime();
            int adapterPosition = getAdapterPosition();
            delegate.onDestinationSelected(adapterPosition);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("jomy", "destinationDatas ssew" + destinationDatas.size());
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
         *   set typeface
         */
        holder.textViewSchedule.setTypeface(typefaceLight);
        holder.textViewTravelTime.setTypeface(typefaceLight);
        holder.mTextView_destinationAssignedBy.setTypeface(typefaceLight);
        holder.mTextView_destinationName.setTypeface(typefaceLight);
        holder.mTextView_destinationScheduled.setTypeface(typefaceLight);
        holder.mButton_checkIncheckOut.setTypeface(typefaceLight);

        /**
         *   displaying travel time
         */
        /*if (elements != null && elements.length > position) {
            String distance = elements[position].getDistance().getText();
            String duration = elements[position].getDuration().getText();

            holder.textViewTravelTime.setText(distance+" "+duration);
            holder.textViewTravelTime.setVisibility(View.VISIBLE);
        } else {
            holder.textViewTravelTime.setVisibility(View.INVISIBLE);
        }*/

        /*float[] distance = new float[2];

        Location.distanceBetween(
                currentLatitude,
                currentLongitude,
                destinationDatas.get(position).getDestinationLatitude(),
                destinationDatas.get(position).getDestinationLongitude(), distance);

        String kilometers = new DecimalFormat("##.#").format(distance[0] / 1000);

        if(currentLatitude!=0)
        holder.textViewTravelTime.setVisibility(View.VISIBLE);
        holder.textViewTravelTime.setText(kilometers + context.getResources().getString(R.string.km));*/


        String travelTime = destinationDatas.get(position).getTravelTime();

        if (travelTime.equals("-1")) {
            holder.textViewTravelTime.setVisibility(View.INVISIBLE);
        } else {
            holder.textViewTravelTime.setVisibility(View.VISIBLE);
            holder.textViewTravelTime.setText(travelTime);
        }

        /**
         *   to display scheduled and unscheduled textview
         */
        String scheduledStatus = destinationDatas.get(position).getScheduledStatus();

        if (scheduledStatus != null) {
            if (scheduledStatus.equals("scheduled") && position == 0) {
                holder.textViewSchedule.setVisibility(View.VISIBLE);
                holder.textViewSchedule.setText("Scheduled Destinations");
            } else if (scheduledStatus.equals("unscheduled")) {
                holder.textViewSchedule.setVisibility(View.VISIBLE);
                holder.textViewSchedule.setText("Unscheduled Destinations");
            } else {
                holder.textViewSchedule.setVisibility(View.GONE);
            }
        } else {
            holder.textViewSchedule.setVisibility(View.GONE);
        }


        /**
         *   to display assigned Destination Time, get the time difference
         */
        String scheduledTime = null;
        String scheduleDate = destinationDatas.get(position).getScheduleDate();
        if (scheduleDate.length() != 0) {
            scheduledTime = scheduledAt + " " + TimeSettings.getTimeOnly(scheduleDate);
        } else {
            scheduledTime = "Not Scheduled";
        }

        /**
         *   check userID and assignedUserID to display 'assigned by me'
         */
        String assignedUserName = null;
        assignedUserID = destinationDatas.get(position).getAssignedUserID();
        if (userID == assignedUserID) {
            assignedUserName = "Assigned by \nSelf";
        } else {
            assignedUserName = "Assigned by \n" + destinationDatas.get(position).getName();
        }

        holder.mTextView_destinationAssignedBy.setText(assignedUserName);

        holder.mTextView_destinationScheduled.setText(scheduledTime);

        /**
         *   set drawable left icon and setting the size
         */
        holder.mTextView_destinationScheduled.setCompoundDrawables(
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

            if (userTypeID == MenuActivity.CORPORATE_ADMIN) {
                holder.mLinearLayout_checkIncheckOut.setEnabled(false);
                holder.mButton_checkIncheckOut.setEnabled(false);
            } else {
                holder.mLinearLayout_checkIncheckOut.setEnabled(true);
                holder.mButton_checkIncheckOut.setEnabled(true);
            }
            holder.mButton_checkIncheckOut.setText(context.getString(R.string.checkin));
            holder.mButton_checkIncheckOut.setTextColor(context.getResources().getColor(R.color.green));
        } else if (!isCheckOut) {
            holder.mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checkout);

            if (userTypeID == MenuActivity.CORPORATE_ADMIN) {
                holder.mLinearLayout_checkIncheckOut.setEnabled(false);
                holder.mButton_checkIncheckOut.setEnabled(false);
            } else {
                holder.mLinearLayout_checkIncheckOut.setEnabled(true);
                holder.mButton_checkIncheckOut.setEnabled(true);
            }
            holder.mButton_checkIncheckOut.setText(context.getString(R.string.checkout));
            holder.mButton_checkIncheckOut.setTextColor(context.getResources().getColor(R.color.app_color));
        } else {
            String checkedOutTime = destinationDatas.get(position).getCheckedOutReportedDate();
            holder.mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checked_at);
            holder.mLinearLayout_checkIncheckOut.setEnabled(false);
            holder.mButton_checkIncheckOut.setEnabled(false);
            holder.mButton_checkIncheckOut.setText(context.getString(R.string.checkedout_at) + " " + TimeSettings.getTimeOnly(checkedOutTime));
            holder.mButton_checkIncheckOut.setCompoundDrawables(
                    GeneralUtils.scaleDrawable(this.context.getResources().getDrawable(R.drawable.checkedout_tick), 40, 40),
                    null, null, null);
            holder.mButton_checkIncheckOut.setTextColor(Color.WHITE);

        }

        if ((destinationDatas.get(position).getCheckoutComment() != null && destinationDatas.get(position).getCheckoutComment().trim().length() > 0) ||
                (destinationDatas.get(position).getComment() != null && destinationDatas.get(position).getComment().trim().length() > 0))
            holder.mCheckin_notes.setVisibility(View.VISIBLE);
        else
            holder.mCheckin_notes.setVisibility(View.GONE);

        /**
         *   on clicking below LinearLayout or Button , call CheckInCheckOut API
         */
        holder.mLinearLayout_checkIncheckOut.setTag(position);
        holder.mLinearLayout_checkIncheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                show_admin_comment = false;
                admin_comment = null;

                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();
                int destinationPosition = (int) v.getTag();

                boolean checkIn = destinationDatas.get(position).isCheckedIn();
                boolean checkOut = destinationDatas.get(position).isCheckedOut();

                if (checkIn == true && checkOut == false) {
                    checkOutNote(context, destinationPosition, destinationDatas.get(position).isCheckedIn());
                } else {
                    show_admin_comment = true;
                    admin_comment = destinationDatas.get(position).getComment();
                    delegate.callCheckInCheckOutService("", destinationPosition, destinationDatas.get(position).isCheckedIn());
                }
            }
        });
        holder.mCheckin_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                         Toast.makeText(context,""+ destinationDatas.get(position).getCheckoutComment(),Toast.LENGTH_SHORT).show();
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();
                String message = "";

                String adminComment = destinationDatas.get(position).getComment();
                String userComment = destinationDatas.get(position).getCheckoutComment();

                commentView(adminComment, userComment);

                /*if ((destinationDatas.get(position).getCheckoutComment() != null && destinationDatas.get(position).getCheckoutComment().trim().length() > 0))
                    message = destinationDatas.get(position).getCheckoutComment();
                else
                    message = destinationDatas.get(position).getComment();

                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MenuActivity.ContextMenuActivity);
                alertDialogBuilderUserInput.setTitle("" + context.getResources().getString(R.string.app_name));

                alertDialogBuilderUserInput.setMessage("" + message);


                alertDialogBuilderUserInput
                        .setCancelable(true)
                        .setPositiveButton("" + context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });
                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();*/
            }
        });

        holder.mButton_checkIncheckOut.setTag(position);
        holder.mButton_checkIncheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                show_admin_comment = false;
                admin_comment = null;

                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();
                int destinationPosition = (int) v.getTag();

                boolean checkIn = destinationDatas.get(position).isCheckedIn();
                boolean checkOut = destinationDatas.get(position).isCheckedOut();

                if (checkIn == true && checkOut == false) {
                    checkOutNote(context, destinationPosition, destinationDatas.get(position).isCheckedIn());
                } else {
                    show_admin_comment = true;
                    admin_comment = destinationDatas.get(position).getComment();
                    delegate.callCheckInCheckOutService("", destinationPosition, destinationDatas.get(position).isCheckedIn());
                }
            }
        });

    }

    public void checkOutNote(Context context, final int destinationPosition, final boolean isChecked) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_user_input, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput.setTitle(context.getString(R.string.app_name));

        final EditText mEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        mEditText.addTextChangedListener(new AddTextWatcher(mEditText));
        mEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditText.setHint("Check Out Note");
        mEditText.requestFocus();

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String checkOutNote = mEditText.getText().toString();
                        dialog.dismiss();
                        delegate.callCheckInCheckOutService(checkOutNote, destinationPosition, isChecked);

                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

    }

    private void commentView(String adminComment, String userComment) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.comment_view, null);
        AlertDialog.Builder alertDialogCommentView = new AlertDialog.Builder(context);
        alertDialogCommentView.setView(mView);

        TextView textViewAdminComment = (TextView) mView.findViewById(R.id.textViewAdminComment);
        TextView textViewUserComment = (TextView) mView.findViewById(R.id.textViewUserComment);

        if (adminComment.length() > 0) {
            textViewAdminComment.setText(adminComment);
        } else {
            textViewAdminComment.setText("No admin comment available.");
        }

        if (userComment.length() > 0) {
            textViewUserComment.setText(userComment);
        } else {
            textViewUserComment.setText("No user comment available.");
        }

        alertDialogCommentView.setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogCommentView.create();
        alertDialog.show();
    }

}
