package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
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

public class AnalysisBarChart extends MainActivity {
    private final String readURL = "http://10.0.2.2:80/PHP-Backend/api/post/feed.php";
    private final int numClass = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_bar_chart);
        //Getting input for bar chart
        System.out.println(postList.size() + " here");
        System.out.println(postList.get(0).getSuggested_price() + "here");
        //System.out.println(numberOfBooksThree + "After function");
        barChartGenerator();



    }
    private double getMax(){
        double result = -1;
        for(int i = 0; i < postList.size(); i++){
            if(postList.get(i).getSuggested_price() > result)
                result = postList.get(i).getSuggested_price();
        }
        return result;
    }
    private double getMin(){
        double result = Double.MAX_VALUE;
        for(int i = 0; i < postList.size(); i++){
            if(postList.get(i).getSuggested_price() < result)
                result = postList.get(i).getSuggested_price();
        }
        return result;
    }
    /**
     * Return Class interval of the histogram, the returned list should have numClass + 1 elements
     * @param max
     * @param min
     * @param numClass
     * @return
     */
    private double[] getClassInterval(double max, double min, int numClass){
        double[] result = new double[numClass + 1];
        double interval = (max-min) / 4;
        for(int i = 0; i <= numClass; i++){
            result[i] = min+interval*i;
            System.out.println(result[i] + " interval");
        }
        return result;
    }
    private ArrayList<BarEntry> assignClassInterval(double[] interval){
        ArrayList<BarEntry> frequency = new ArrayList<>();
        double inter = (interval[1] - interval[0]);
        for(int i = 1; i < interval.length; i++){
            frequency.add(new BarEntry((float)(inter/2 + interval[0] + inter*(i-1)), 0));
        }
        for(Post i:postList){
            for(int j = 1; j < interval.length; j++){
                if(i.getSuggested_price() <= interval[j]){
                    frequency.get(j-1).setY(frequency.get(j-1).getY()+1);
                    break;
                }
            }
        }
        for(int i = 0; i < frequency.size();i++){
            System.out.println("frequency arrayList: " + i + "position: " + frequency.get(i).getX());
        }
        return frequency;
    }
    private void barChartGenerator()
    {
        //Bar Chart Generator
        BarChart barChart = findViewById(R.id.barChart);
        BarDataSet barDataSet = new BarDataSet(assignClassInterval(getClassInterval(getMax(), getMin(), numClass)), "Price");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(100f);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.animateY(2000);
    }

}