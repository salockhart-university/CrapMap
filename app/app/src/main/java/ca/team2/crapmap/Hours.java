package ca.team2.crapmap;

import java.util.Calendar;

/**
 * Created by geoffreycaven on 2017-03-23.
 */

public class Hours {
    private int day_of_week;
    private double open;
    private double close;

    public Hours(int day_of_week, double open, double close) {
        this.day_of_week = day_of_week;
        this.open = open;
        this.close = close;
    }

    public Hours(int day_of_week) {
        this.day_of_week = day_of_week;
        this.open = 9;
        this.close = 17;
    }

    public int getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(int day_of_week) {
        this.day_of_week = day_of_week;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    @Override
    public String toString() {
        return "Hours{" +
                "day_of_week=" + day_of_week +
                ", open=" + open +
                ", close=" + close +
                '}';
    }
}
