package com.yiliao.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.yiliao.chat.R;
import com.yiliao.chat.constant.Constant;

import java.util.Calendar;


/**
 * Created by cxf on 2017/8/8.
 */

public class DialogUitl {
    public static final int INPUT_TYPE_TEXT = 0;
    public static final int INPUT_TYPE_NUMBER = 1;
    public static final int INPUT_TYPE_NUMBER_PASSWORD = 2;
    public static final int INPUT_TYPE_TEXT_PASSWORD = 3;


    public static void showDatePickerDialog(Context context, final DataPickerCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        if (Constant.hideHomeNearAndNew()){
            dialog.setContentView(R.layout.yuebo_dialog_date_picker);
        }else {
            dialog.setContentView(R.layout.dialog_date_picker);
        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        final Calendar c = Calendar.getInstance();
        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month, dayOfMonth);
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_confirm) {
                    if (callback != null) {
                        String result = DateFormat.format("yyyy-MM-dd", c).toString();
                        callback.onConfirmClick(result);
                        dialog.dismiss();

                    }
                } else {
                    dialog.dismiss();
                }
            }
        };
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(listener);
        dialog.show();
    }

    static int hour;
    static int minutes;

    public static void showTimePickerDialog(Context context, final DataPickerCallback callback) {

        final Dialog dialog = new Dialog(context, R.style.dialog);
        if (Constant.hideHomeNearAndNew()){
            dialog.setContentView(R.layout.yuebo_dialog_time_picker);
        }else {
            dialog.setContentView(R.layout.dialog_time_picker);
        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true); //设置24小时制
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minutes);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                minutes = minute;
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_confirm) {
                    if (callback != null) {
                        String h = null;
                        String m = null;
                        if (hour < 10) {
                            h = "0" + hour;
                        } else {
                            h = hour + "";
                        }
                        if (minutes < 10) {
                            m = "0" + minutes;
                        } else {
                            m = minutes + "";
                        }
                        String result = h + ":" + m;
                        callback.onConfirmClick(result);
                        dialog.dismiss();

                    }
                } else {
                    dialog.dismiss();
                }
            }
        };
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(listener);
        dialog.show();
    }


    public interface DataPickerCallback {
        void onConfirmClick(String date);
    }

    /**
     * 账号被挤掉
     */
    public static void showAccountOutDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_account_out);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
