package com.zichri.ilearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ChartView chartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chartView = findViewById(R.id.chartView);
        initChartView();
    }

    private void initChartView() {
        List<ChartDataBean> beans = new ArrayList<>();

        beans.add(new ChartDataBean(0.2f, "11.10"));
        beans.add(new ChartDataBean(0.6f, "11.11"));
        beans.add(new ChartDataBean(1.3f, "11.12"));
        beans.add(new ChartDataBean(0.1f, "11.13"));
        beans.add(new ChartDataBean(0.4f, "11.14"));
        beans.add(new ChartDataBean(1.0f, "11.15"));
        beans.add(new ChartDataBean(0.8f, "11.16"));
        beans.add(new ChartDataBean(1.7f, "11.17"));
        beans.add(new ChartDataBean(0.5f, "11.18"));
        beans.add(new ChartDataBean(0.3f, "11.19"));
        beans.add(new ChartDataBean(0.9f, "11.20"));
//        beans.add(new QrayExponentBean(0.2f, 100));
//        beans.add(new QrayExponentBean(0.4f, 100));
//        beans.add(new QrayExponentBean(0.6f, 100));
//        beans.add(new QrayExponentBean(0.8f, 100));
//        beans.add(new QrayExponentBean(1.0f, 100));
//        beans.add(new QrayExponentBean(0.8f, 100));
//        beans.add(new QrayExponentBean(0.6f, 100));
//        beans.add(new QrayExponentBean(0.4f, 100));
//        beans.add(new QrayExponentBean(0.2f, 100));
        chartView.setData(beans);
    }


}
