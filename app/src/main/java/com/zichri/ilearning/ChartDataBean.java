package com.zichri.ilearning;

public class ChartDataBean {
    private float indexValue;//Qray值
    private float indexChange;//指数变化
    private String trainingDateStr;//训练日期 格式 yyyy-MM-dd

    public ChartDataBean(float qrayExponent, String time) {
        this.indexValue = qrayExponent;
        this.trainingDateStr = time;
    }

    public float getQrayExponent() {
        return indexValue;
    }

    public float getIndexChange() {
        return indexChange;
    }

    public String getTime() {
        return trainingDateStr;
    }
}
