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

import com.example.g1.R;
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


public class YearIncomeFragment extends Fragment {
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
        activity.setTitle("年營收統計");
        return inflater.inflate(R.layout.fragment_year_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BarChart barChart = view.findViewById(R.id.barChart);

        barChart.setBackgroundColor(Color.WHITE);
        XAxis xAxis = barChart.getXAxis();//取得X軸標籤文字
        xAxis.setAxisMaximum(10);//設定最大值十年內

        YAxis yAxisLeft = barChart.getAxisLeft();//取左側Y軸物件
        yAxisLeft.setAxisMaximum(30000);//設定左側Y軸最大值

        YAxis yAxisRight = barChart.getAxisRight();//取右側Y軸物件
        yAxisRight.setEnabled(false);//是否取得Y軸

        Description description = new Description();
        description.setText("年營收統計表");
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
        incomeEntries.add(new BarEntry(1, 20000));
        incomeEntries.add(new BarEntry(2, 17000));
        incomeEntries.add(new BarEntry(3, 27000));
        incomeEntries.add(new BarEntry(4, 23000));
        incomeEntries.add(new BarEntry(5, 15000));
        incomeEntries.add(new BarEntry(6, 20000));
        incomeEntries.add(new BarEntry(7, 29000));
        incomeEntries.add(new BarEntry(8,26000 ));
        incomeEntries.add(new BarEntry(9, 9000));
        incomeEntries.add(new BarEntry(10, 26000));
        return incomeEntries;
    }

}
