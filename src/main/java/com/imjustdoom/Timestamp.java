package com.imjustdoom;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Timestamp {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    private LocalDateTime time;

    public Timestamp(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;

        this.time = LocalDateTime.of(year, month, day, hour, minute, second);
    }

    public Timestamp(String timestamp) {
        String year = timestamp.substring(0, 4);
        String month = timestamp.substring(4, 6);
        String day = timestamp.substring(6, 8);
        String hour = timestamp.substring(8, 10);
        String minute = timestamp.substring(10, 12);
        String second = timestamp.substring(12, 14);

        this.time = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));

        this.year = this.time.getYear();
        this.month = this.time.getMonthValue();
        this.day = this.time.getDayOfMonth();
        this.hour = this.time.getHour();
        this.minute = this.time.getMinute();
        this.second = this.time.getSecond();
    }

    private void getDatesFromDateTime() {
        this.year = this.time.getYear();
        this.month = this.time.getMonthValue();
        this.day = this.time.getDayOfMonth();
        this.hour = this.time.getHour();
        this.minute = this.time.getMinute();
        this.second = this.time.getSecond();
    }

    public void plusYears(int years) {
        this.time = this.time.plusYears(years);
        getDatesFromDateTime();
        //this.month = 1;
    }

    public void plusMonths(int months) {
        int prevMonth = this.month;
        this.time = this.time.plusMonths(months);
        getDatesFromDateTime();
        //this.day = 1;
    }

    public void plusDays(int days) {
        int prevDay = this.day;
        this.time = this.time.plusDays(days);
        getDatesFromDateTime();
        //this.hour = 0;
    }

    public void plusHours(int hours) {
        int prevHour = this.hour;
        this.time = this.time.plusHours(hours);
        getDatesFromDateTime();
        //this.minute = 0;
    }

    public void plusMinutes(int minutes) {
        int prevMinute = this.minute;
        this.time = this.time.plusMinutes(minutes);
        getDatesFromDateTime();
        //this.second = 0;
    }

    public void plusSeconds(int seconds) {
        int prevSecond = this.second;
        this.time = this.time.plusSeconds(seconds);
        getDatesFromDateTime();
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

    public enum Time {
        YEAR(4),
        MONTH(6),
        DAY(8),
        HOUR(10),
        MINUTE(12),
        SECOND(14);

        final int sub;

        Time(int sub) {
            this.sub = sub;
        }
    }
}
