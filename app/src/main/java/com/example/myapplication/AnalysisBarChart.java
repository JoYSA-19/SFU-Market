package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * In the Setting Activity, click "Data Analysis" to show a Histogram of Cost distribution
 */
public class AnalysisBarChart extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_bar_chart);
        barChartGenerator();
    }

    /**
     * Get Maximum price value in the Postlist
     * @return Maximum price value in the Postlist
     */
    private double getMax() {
        double result = -1;
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getSuggested_price() > result)
                result = postList.get(i).getSuggested_price();
        }
        return result;
    }

    /**
     * Get Minimum price value in the Postlist
     * @return Minimum price value in the Postlist
     */
    private double getMin() {
        double result = Double.MAX_VALUE;
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getSuggested_price() < result)
                result = postList.get(i).getSuggested_price();
        }
        return result;
    }

    /**
     * Return Class interval of the histogram, the returned list should have numClass + 1 elements
     *
     * @param max maximum value of price
     * @param min minimum value of price
     * @param numClass number of classes for the histogram
     * @return class interval (Ex. 0, 100, 200, 300, 400) The last value will be the maximum value
     */
    private double[] getClassInterval(double max, double min, int numClass) {
        double[] result = new double[numClass + 1];
        double interval = (max - min) / 4;
        for (int i = 0; i <= numClass; i++) {
            result[i] = min + interval * i;
        }
        return result;
    }

    /**
     * Assign the ArrayList for the BarChartGenerator
     * @param interval double array of class interval
     * @return the ArrayList for the BarChartGenerator
     */
    private ArrayList<BarEntry> assignClassInterval(double[] interval) {
        ArrayList<BarEntry> frequency = new ArrayList<>();
        double inter = (interval[1] - interval[0]);
        for (int i = 1; i < interval.length; i++) {
            frequency.add(new BarEntry((float) (inter / 2 + interval[0] + inter * (i - 1)), 0));
        }
        for (Post i : postList) {
            for (int j = 1; j < interval.length; j++) {
                if (i.getSuggested_price() <= interval[j]) {
                    frequency.get(j - 1).setY(frequency.get(j - 1).getY() + 1);
                    break;
                }
            }
        }
        return frequency;
    }

    /**
     * Generate the BarChart, in our case, a histogram of cost distribution
     */
    private void barChartGenerator() {
        BarChart barChart = findViewById(R.id.barChart);
        //populate the bar chart with the post information
        int numClass = 4;
        BarDataSet barDataSet = new BarDataSet(assignClassInterval(getClassInterval(getMax(), getMin(), numClass)), "Frequency");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(30f);
        Description description = new Description();
        description.setTextSize(16f);
        description.setText("Frequency vs Cost Graph");
        barChart.setDescription(description);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.animateY(2000);
    }

}