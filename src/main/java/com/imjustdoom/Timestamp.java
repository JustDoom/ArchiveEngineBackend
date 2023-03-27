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

    public void plusYears(int years) {
        this.time = this.time.plusYears(years);
        this.year = this.time.getYear();
    }

    public void plusMonths(int months) {
        this.time = this.time.plusMonths(months);
        this.month = this.time.getMonthValue();
    }

    public void plusDays(int days) {
        this.time = this.time.plusDays(days);
        this.day = this.time.getDayOfMonth();
    }

    public void plusHours(int hours) {
        this.time = this.time.plusHours(hours);
        this.hour = this.time.getHour();
    }

    public void plusMinutes(int minutes) {
        this.time = this.time.plusMinutes(minutes);
        this.minute = this.time.getMinute();
    }

    public void plusSeconds(int seconds) {
        this.time = this.time.plusSeconds(seconds);
        this.second = this.time.getSecond();
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
