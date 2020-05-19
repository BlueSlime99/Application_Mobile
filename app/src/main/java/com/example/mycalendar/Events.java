package com.example.mycalendar;

public class Events {
    String Events, DATE, TIME, MONTH, YEAR;


    public Events(String events, String DATE, String TIME, String MONTH, String YEAR) {
        Events = events;
        this.DATE = DATE;
        this.TIME = TIME;
        this.MONTH = MONTH;
        this.YEAR = YEAR;
    }


    public String getEvents() {
        return Events;
    }

    public void setEvents(String events) {
        Events = events;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getMONTH() {
        return MONTH;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }

    public String getYEAR() {
        return YEAR;
    }

    public void setYEAR(String YEAR) {
        this.YEAR = YEAR;
    }


}