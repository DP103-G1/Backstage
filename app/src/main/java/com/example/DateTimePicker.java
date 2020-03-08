package com.example;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.NumberPicker;


import com.example.main.R;

import java.util.Calendar;

public class DateTimePicker extends FrameLayout {
    private NumberPicker yearSpinner;
    private NumberPicker monthSpinner;
    private NumberPicker daySpinner;
    private Calendar date, oDate;
    private OnDateTimeChangedListener onDateTimeChangedListener;

    public DateTimePicker(Context context, Calendar oDate, DateTimePickerType dateTimePickerType) {
        super(context);
        this.date = Calendar.getInstance();
        if (dateTimePickerType.equals(DateTimePickerType.YEAR)) {
            date.add(Calendar.YEAR, -1);
        }
        this.oDate = oDate;

        inflate(context, R.layout.date_time_piker, this);

        yearSpinner = this.findViewById(R.id.npYear);
        initYear();
        yearSpinner.setWrapSelectorWheel(false);
        yearSpinner.setOnValueChangedListener(onYearChangedLintener);

        monthSpinner = this.findViewById(R.id.npMonth);
        initMonth();
        monthSpinner.dispatchSetSelected(false);
        monthSpinner.setOnValueChangedListener(onMonthChangedLintener);

        daySpinner = this.findViewById(R.id.npDay);
        initDay();
        daySpinner.dispatchSetSelected(false);
        daySpinner.setOnValueChangedListener(onDayChangedLintener);
        onDateTimeChanged();
        switch (dateTimePickerType) {
            case YEAR:
                monthSpinner.setVisibility(GONE);
            case MONTH:
                daySpinner.setVisibility(GONE);
        }
    }

    private NumberPicker.OnValueChangeListener onYearChangedLintener = (picker, oldVal, newVal) -> {
        oDate = Calendar.getInstance();
        oDate.set(Calendar.YEAR, yearSpinner.getValue());
        initDay();
        onDateTimeChanged();
    };

    private NumberPicker.OnValueChangeListener onMonthChangedLintener = (picker, oldVal, newVal) -> {
        Calendar judgeCal = Calendar.getInstance();
        judgeCal.setTime(oDate.getTime());
        judgeCal.set(Calendar.DAY_OF_MONTH, 1);
        judgeCal.set(Calendar.MONTH, newVal);
        int maxDay = judgeCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (oDate.get(Calendar.DAY_OF_MONTH) > maxDay) {
            oDate.set(Calendar.DAY_OF_MONTH, maxDay);
        }
        oDate.set(Calendar.MONTH, monthSpinner.getValue());
        initDay();
        onDateTimeChanged();
    };

    private NumberPicker.OnValueChangeListener onDayChangedLintener = (picker, oldVal, newVal) -> {
        oDate.set(Calendar.DAY_OF_MONTH, daySpinner.getValue());
        onDateTimeChanged();
    };

    private void initMonth() {
        monthSpinner.setMaxValue(11);
        monthSpinner.setMinValue(0);
        monthSpinner.setFormatter(value -> String.valueOf(value + 1));
        monthSpinner.setValue(oDate.get(Calendar.MONTH));
    }

    private void initDay() {
        daySpinner.setMaxValue(oDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        daySpinner.setMinValue(1);
        daySpinner.setValue(oDate.get(Calendar.DAY_OF_MONTH));
    }

    private void initYear() {
        yearSpinner.setMaxValue(date.get(Calendar.YEAR));
        yearSpinner.setMinValue(date.get(Calendar.YEAR) - 3);
        yearSpinner.setValue(oDate.get(Calendar.YEAR));
    }

    public interface OnDateTimeChangedListener {
        void onDateTimeChanged(DateTimePicker view, int year, int month, int day);
    }

    public void setOnDateTimeChangedListener(OnDateTimeChangedListener callBack) {
        onDateTimeChangedListener = callBack;
    }

    public void onDateTimeChanged() {
        if (onDateTimeChangedListener != null) {
            onDateTimeChangedListener.onDateTimeChanged(this, oDate.get(Calendar.YEAR),
                    oDate.get(Calendar.MONTH), oDate.get(Calendar.DAY_OF_MONTH));
        }
    }

}
