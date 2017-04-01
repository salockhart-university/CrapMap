package ca.team2.crapmap.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by geoffreycaven on 2017-03-23.
 */

public class Hours implements Serializable {
    private int day_of_week;
    private double open;
    private double close;

    public Hours(int day_of_week, double open, double close) {
        this.day_of_week = day_of_week;
        this.open = open;
        this.close = close;
    }

    public Hours(String day_of_week, double open, double close) {
        this.day_of_week = convertToEnum(day_of_week);
        this.open = open;
        this.close = close;
    }

    public Hours(int day_of_week) {
        this.day_of_week = day_of_week;
        this.open = 9;
        this.close = 17;
    }

    public Hours(String day_of_week) {
        this.day_of_week = convertToEnum(day_of_week);
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

    private int convertToEnum(String day) {
        switch (day.toLowerCase()) {
            case "sun":
            case "sunday":
                return Calendar.SUNDAY;
            case "mon":
            case "monday":
                return Calendar.MONDAY;
            case "tues":
            case "tuesday":
                return Calendar.TUESDAY;
            case "wed":
            case "wednesday":
                return Calendar.WEDNESDAY;
            case "thurs":
            case "thursday":
                return Calendar.THURSDAY;
            case "fri":
            case "friday":
                return Calendar.FRIDAY;
            case "sat":
            case "saturday":
                return Calendar.SATURDAY;
            default:
                throw new RuntimeException("Invalid day code provided: " + day);
        }
    }

    public String getforAPIDay_of_week() {
        switch(day_of_week) {
            case Calendar.SUNDAY:
                return "sun";
            case Calendar.MONDAY:
                return "mon";
            case Calendar.TUESDAY:
                return "tues";
            case Calendar.WEDNESDAY:
                return "wed";
            case Calendar.THURSDAY:
                return "thurs";
            case Calendar.FRIDAY:
                return "fri";
            case Calendar.SATURDAY:
                return "sat";
            default:
                throw new RuntimeException("Invalid day code in hours object: " + day_of_week);
        }
    }
}
