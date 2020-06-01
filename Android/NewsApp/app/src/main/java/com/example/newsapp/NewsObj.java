package com.example.newsapp;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;


public class NewsObj {
    private String id;
    private String url;
    private String title;
    private String desc;
    private String img;
    private String time;
    private String tag;
    private ZonedDateTime timeLA;
    private ZoneId zoneId;

    public NewsObj(String id, String url, String title, String desc, String img, String time, String tag) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.desc = desc;
        this.img = img;
        this.time = time;
        this.tag = tag;
        zoneId = ZoneId.of("America/Los_Angeles");
        this.timeLA = convertTime(time);
    }

    public NewsObj(String infostr) {
        String[] info = infostr.split("////");
        this.id = info[0];
        this.title = info[1];
        this.img = info[2];
        this.time = info[3];
        this.tag = info[4];
        this.url = info[5];
        zoneId = ZoneId.of("America/Los_Angeles");
        this.timeLA = convertTime(time);
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImg() {
        return img;
    }

    private ZonedDateTime convertTime(String time) {
        Instant timestamp = Instant.parse(time);

        return timestamp.atZone(zoneId);
    }
//    public String getTime() {
//        String date = getDate();
//        int hour = timeLA.getHour();
//
//    }

    public String getTimeFromNow() {
        Duration d = Duration.between(timeLA, ZonedDateTime.now(zoneId));
        long seconds = d.getSeconds();
        if (seconds / 86400 > 0) {
            return (seconds / 86400) + "d";
        }
        else if (seconds / 3600 > 0) {
            return (seconds / 3600) + "h";
        }
        else if (seconds / 60 > 0) {
            return (seconds / 60) + "m";
        }
        else {
            return seconds + "s";
        }
    }

    public String getDate() {
        int day = timeLA.getDayOfMonth();
        Month month = timeLA.getMonth();
        int year = timeLA.getYear();
        String monthstr = month.toString().substring(0, 1) + month.toString().toLowerCase().substring(1);
        return day + " " + monthstr + " " + year;
    }

    public String getDateWithoutYear() {
        int day = timeLA.getDayOfMonth();
        Month month = timeLA.getMonth();
        String monthstr = month.toString().substring(0, 1) + month.toString().toLowerCase().substring(1);
        return day + " " + monthstr + " ";
    }

    public String getTag() {
        return tag;
    }

    public String getTimePeriod() {
        return "";
    }


    @NonNull
    @Override
    public String toString() {
        return id + "////" + title + "////" + img + "////" + time + "////" + tag + "////" + url;
    }

}
