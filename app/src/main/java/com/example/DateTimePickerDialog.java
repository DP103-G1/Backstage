package com.example;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateUtils;

import java.util.Calendar;


public class DateTimePickerDialog extends AlertDialog {
    private DateTimePicker dateTimePicker;
    private Calendar date = Calendar.getInstance();
    private OnDateTimeSetListener onDateTimeSetListener;

    public DateTimePickerDialog(Context context, long date, DateTimePickerType dateTimePickerType) {
        super(context);
        this.date.setTimeInMillis(date);
        setTitle("請設定時間");
        dateTimePicker = new DateTimePicker(context, this.date, dateTimePickerType);
        setView(dateTimePicker);
        dateTimePicker.setOnDateTimeChangedListener((view, year, month, day) -> {
            this.date.set(Calendar.YEAR, year);
            this.date.set(Calendar.MONTH, month);
            this.date.set(Calendar.DAY_OF_MONTH, day);
            this.date.set(Calendar.SECOND, 0);
        });
        setButton(BUTTON_POSITIVE, "設置", this::onClick);
        setButton(BUTTON_NEGATIVE, "取消", (OnClickListener) null);
        dateTimePicker.onDateTimeChanged();
    }

    public interface OnDateTimeSetListener {
        void onDateTimeSet(AlertDialog dialog, Long date);
    }

    public void setOnDateTimeSetListener(OnDateTimeSetListener callBack) {
        onDateTimeSetListener = callBack;
    }

    public void onClick(DialogInterface arg0, int arg1) {
        if (onDateTimeSetListener != null) {
            onDateTimeSetListener.onDateTimeSet(this, date.getTimeInMillis());
        }
    }
}
