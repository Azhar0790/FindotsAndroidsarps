package com.knowall.findots.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.knowall.findots.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

/**
 * Created by jpaulose on 7/22/2016.
 */
public class MaterialDatePickerDialogCustom extends DatePickerDialog {

    private OnDateScheduledListener mOnDateScheduledListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = super.onCreateView(inflater, container, state);
        LinearLayout buttonContainer = (LinearLayout) view.findViewById(
                com.wdullaer.materialdatetimepicker.R.id.done_background);
        View clearButton = inflater.inflate(R.layout.material_datepicker_extrabutton,
                buttonContainer, false);
        clearButton.setOnClickListener(new ClearClickListener());
        buttonContainer.addView(clearButton, 0);

        return view;
    }

    public static MaterialDatePickerDialogCustom newInstance() {
        MaterialDatePickerDialogCustom ret = new MaterialDatePickerDialogCustom();
        return ret;
    }

    public void setOnDateScheduledListener(OnDateScheduledListener listener) {
        mOnDateScheduledListener = listener;
    }

    public OnDateScheduledListener getOnDateScheduledListener() {
        return mOnDateScheduledListener;
    }

    public interface OnDateScheduledListener {

        /**
         * @param view The view associated with this listener.
         */
        void onDateScheduled(MaterialDatePickerDialogCustom view);

    }

    private class ClearClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Log.d("jomy","schedule...");
            OnDateScheduledListener listener = getOnDateScheduledListener();
            if (listener != null) {
                listener.onDateScheduled(MaterialDatePickerDialogCustom.this);
            }
            dismiss();
        }

    }

}
