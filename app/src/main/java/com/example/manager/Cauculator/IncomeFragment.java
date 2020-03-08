package com.example.manager.Cauculator;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.main.R;
import com.example.Common;
import com.example.DateTimePickerDialog;
import com.example.DateTimePickerType;
import com.example.g1.R;
import com.example.task.CommonTask;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Predicate;


public class IncomeFragment extends Fragment {
    private static final String TAG = "TAG_IncomeFragment";
    private Activity activity;
    private CommonTask IncomeSearchTask;
    private Calendar calendar = Calendar.getInstance();
    private List<Order> orders;
    private BarChart barChart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("日營收統計");
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orders = new ArrayList<>();
        orders = getOrders();

        Button btTest = view.findViewById(R.id.btTest);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        btTest.setText(format.format(calendar.getTime()));
        btTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog dialog = new DateTimePickerDialog(activity, calendar.getTimeInMillis(), DateTimePickerType.DAY);
                dialog.setOnDateTimeSetListener(new DateTimePickerDialog.OnDateTimeSetListener() {
                    @Override
                    public void onDateTimeSet(AlertDialog dialog, Long date) {
                        calendar.setTimeInMillis(date);
                        btTest.setText(format.format(calendar.getTime()));
                        orders = getOrders();
                        calAndShow();
                    }
                });
                dialog.show();
            }
        });
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//    }
        barChart = view.findViewById(R.id.barChart);
        Description description = new Description();//版面設定
        description.setEnabled(false);
        barChart.setDescription(description);
        calAndShow();
    }

    private void calAndShow() {
        List<BarEntry> barEntries = getEntries();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(21);
        xAxis.setAxisMinimum(8);
        xAxis.setTextSize(13);
        xAxis.setTextColor(getResources().getColor(R.color.colortext));
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setTextSize(13);
        yAxisLeft.setTextColor(getResources().getColor(R.color.colortext));
        yAxisLeft.setAxisMaximum((float) (barEntries.stream()
                .mapToDouble(v -> v.getY()).max().orElse(0) * 1.2));
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        BarDataSet barDataSet = new BarDataSet(barEntries, "當日營業額");
        BarData barData = new BarData(barDataSet);
        barDataSet.setColor(getResources().getColor(R.color.normalText));
        barData.setValueTextColor(getResources().getColor(R.color.chartData));
        barData.setValueTextSize(13);
        barData.setValueTypeface(Typeface.DEFAULT);
        barChart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        barChart.setDrawGridBackground(false);
        barChart.animateY(1500, Easing.Linear);
        barChart.animateX(700, Easing.Linear);
        barChart.setData(barData);
        barChart.invalidate();

        Legend legend = barChart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.chartData));
        legend.setTextSize(14);
        legend.setTypeface(Typeface.DEFAULT);
    }

    private List<BarEntry> getEntries() {
        List<BarEntry> barEntries = new ArrayList<>();
        Calendar c = new Calendar.Builder().setInstant(calendar.getTime()).build();
        c.set(Calendar.HOUR_OF_DAY, 9);
        int index = 9;
        while (index <= 20) {
            long minTime = c.getTimeInMillis();
            c.add(Calendar.HOUR_OF_DAY, 1);
            long maxTime = c.getTimeInMillis();
            int sum = orders.stream().filter(order -> order.getORD_TIME().getTime() >= minTime
                    && order.getORD_TIME().getTime() < maxTime)
                    .mapToInt(v -> v.getORD_TOTAL()).sum();
            BarEntry barEntry = new BarEntry(index, sum);
            barEntries.add(barEntry);
            index++;
        }
        return barEntries;
    }

    private List<Order> getOrders() {
        List<Order> orders = null;
        if (Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "/OrderServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","search");
            jsonObject.addProperty("calendar", calendar.getTimeInMillis());
            jsonObject.addProperty("type", "day");
            String jsonOut = jsonObject.toString();
            IncomeSearchTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = IncomeSearchTask.execute().get();
                Type listType = new TypeToken<List<Order>>(){
                }.getType();
                orders = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(jsonIn,listType);
            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
        }else {
            Common.showToast(activity,"R.string.textNoNetWork");
        }
        return orders;
    }
}
