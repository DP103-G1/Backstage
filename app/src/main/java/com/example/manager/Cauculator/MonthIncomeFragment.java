package com.example.manager.Cauculator;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.main.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;


public class MonthIncomeFragment extends Fragment {
    private static final String TAG = "TAG_BarChart";
    private Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("月營收統計");
        return inflater.inflate(R.layout.fragment_month_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BarChart barChart = view.findViewById(R.id.barChart);

        barChart.setBackgroundColor(Color.WHITE);
        XAxis xAxis = barChart.getXAxis();//取得X軸標籤文字
        xAxis.setAxisMaximum(12);//設定最大值12個月份

        YAxis yAxisLeft = barChart.getAxisLeft();//取左側Y軸物件
        yAxisLeft.setAxisMaximum(3500);//設定左側Y軸最大值

        YAxis yAxisRight = barChart.getAxisRight();//取右側Y軸物件
        yAxisRight.setEnabled(false);//是否取得Y軸

        Description description = new Description();
        description.setText("月營收統計表");
        description.setTextSize(16);
        barChart.setDescription(description);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "entry: " + e.toString() + "highlight: " + h.toString());
                String text = (int) e.getX() + "\n" + (int) e.getY();
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        List<BarEntry> incomeEntries = getIncomeEntries();

        BarDataSet barDataSetIncome = new BarDataSet(incomeEntries, "Income");
        barDataSetIncome.setColor(Color.GRAY);
        barDataSetIncome.setHighLightColor(Color.YELLOW);
        barDataSetIncome.setValueTextColor(Color.BLACK);
        barDataSetIncome.setValueTextSize(10);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSetIncome);

        BarData barData = new BarData(dataSets);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private List<BarEntry> getIncomeEntries() {
        List<BarEntry> incomeEntries = new ArrayList<>();
        incomeEntries.add(new BarEntry(1, 3000));
        incomeEntries.add(new BarEntry(2, 2600));
        incomeEntries.add(new BarEntry(3, 2100));
        incomeEntries.add(new BarEntry(4, 1600));
        incomeEntries.add(new BarEntry(5, 2500));
        incomeEntries.add(new BarEntry(6, 9000));
        incomeEntries.add(new BarEntry(7, 3000));
        incomeEntries.add(new BarEntry(8, 600));
        incomeEntries.add(new BarEntry(9, 1400));
        incomeEntries.add(new BarEntry(10, 2300));
        incomeEntries.add(new BarEntry(11, 2800));
        incomeEntries.add(new BarEntry(12, 2700));
        return incomeEntries;
    }

}
