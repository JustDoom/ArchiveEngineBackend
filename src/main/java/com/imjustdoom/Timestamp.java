package com.imjustdoom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Timestamp {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public Timestamp(String timestamp) {
        String year = timestamp.substring(0, 4);
        String month = timestamp.substring(4, 6);
        String day = timestamp.substring(6, 8);
        String hour = timestamp.substring(8, 10);
        String minute = timestamp.substring(10, 12);
        String second = timestamp.substring(12, 14);

        this.year = Integer.parseInt(year);
        this.month = Integer.parseInt(month);
        this.day = Integer.parseInt(day);
        this.hour = Integer.parseInt(hour);
        this.minute = Integer.parseInt(minute);
        this.second = Integer.parseInt(second);
    }

    @Override
    public String toString() {
        return String.valueOf(year)
                + (month < 10 ? "0" + month : month)
                + (day < 10 ? "0" + day : day)
                + (hour < 10 ? "0" + hour : hour)
                + (minute < 10 ? "0" + minute : minute)
                + (second < 10 ? "0" + second : second);
    }
}
