package com.knowall.findots.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.knowall.findots.R;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

/**
 * Created by jpaulose on 7/22/2016.
 */
public class MaterialTimePickerDialogCustom  extends TimePickerDialog
{

    private OnTimeScheduledListener mOnTimeScheduledListener;

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

    public static MaterialTimePickerDialogCustom newInstance() {
        MaterialTimePickerDialogCustom ret = new MaterialTimePickerDialogCustom();
        return ret;
    }

    public void setOnTimeScheduledListener(OnTimeScheduledListener listener) {
        mOnTimeScheduledListener = listener;
    }

    public OnTimeScheduledListener getOnTimeScheduledListener() {
        return mOnTimeScheduledListener;
    }

    public interface OnTimeScheduledListener {

        /**
         * @param view The view associated with this listener.
         */
        void onTimeScheduled(MaterialTimePickerDialogCustom view);

    }

    private class ClearClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Log.d("jomy","schedule...");
            OnTimeScheduledListener listener = getOnTimeScheduledListener();
            if (listener != null) {
                listener.onTimeScheduled(MaterialTimePickerDialogCustom.this);
            }
            dismiss();
        }

    }

}