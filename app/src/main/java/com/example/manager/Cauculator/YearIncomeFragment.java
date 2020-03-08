package com.example.manager.Cauculator;


import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class YearIncomeFragment extends Fragment {
    private static final String TAG = "TAG_YearIncomeFragment";
    private Activity activity;
    private CommonTask IncomeSearchTask;
    private Calendar calendar = Calendar.getInstance();
    private List<Order> orders;
    private BarChart barChart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        calendar.add(Calendar.YEAR,-1);
        calendar.add(Calendar.MONTH,0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("年營收統計");
        return inflater.inflate(R.layout.fragment_year_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orders = new ArrayList<>();
        orders = getOrders();

        Button btTest = view.findViewById(R.id.btTest);
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        btTest.setText(format.format(calendar.getTime()));
        btTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog dialog = new DateTimePickerDialog(activity, calendar.getTimeInMillis(), DateTimePickerType.YEAR);
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
        barChart = view.findViewById(R.id.barChart);
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);
        calAndShow();
    }

    private void calAndShow() {
        List<BarEntry> barEntries = getEntries();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(13);
        xAxis.setAxisMinimum(0);
        xAxis.setTextColor(13);
        xAxis.setTextColor(getResources().getColor(R.color.colorText));
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setTextSize(13);
        yAxisLeft.setTextColor(getResources().getColor(R.color.colorText));
        yAxisLeft.setAxisMaximum((float) (barEntries.stream()
                .mapToDouble(v -> v.getY()).max().orElse(0) * 1.2));
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        BarDataSet barDataSet = new BarDataSet(barEntries, "年度營業額");
        BarData barData = new BarData(barDataSet);
        barDataSet.setColor(getResources().getColor(R.color.normalText));
        barData.setValueTextColor(getResources().getColor(R.color.chartData));
//        barDataSet.setBarBorderWidth(10f);
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
        c.set(Calendar.MONTH, 0);
        int index = 1;
        while (index <= 12) {
            long minTime = c.getTimeInMillis();
            c.add(Calendar.MONTH, 1);
            long maxTime = c.getTimeInMillis();
            int sum = orders.stream().filter(order -> order.getORD_TIME().getTime() >= minTime
                    && order.getORD_TIME().getTime() < maxTime)//選orders的水流（steam）呼叫filter篩選我們想要的時間
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
            jsonObject.addProperty("type", "year");
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