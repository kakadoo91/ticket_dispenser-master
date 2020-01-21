package com.example.mramir.ticketDispenser;

import android.arch.persistence.room.*;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity(indices={@Index(value="date", unique=true)})
public class Turn {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int uid;

    @NonNull
    @ColumnInfo(name = "date")
    private long date;

    @NonNull
    @ColumnInfo(name = "passport")
    private int passportCounter;

    @NonNull
    @ColumnInfo(name = "proxy")
    private int proxyCounter;

    @NonNull
    @ColumnInfo(name = "visa")
    private int visaCounter;
    @NonNull
    @ColumnInfo(name = "daneshjoo")
    private int daneshjooCounter;

    public Turn() {
        date = new Date().getTime();
        proxyCounter = 1;
        passportCounter = 1;
        visaCounter = 1;
        daneshjooCounter =1;
    }

    public long getDate() {
        return date;
    }

    @NonNull
    public int getUid() {
        return uid;
    }

    public void setUid(@NonNull int uid) {
        this.uid = uid;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getPassportCounter() {
        return passportCounter;
    }

    public void setPassportCounter(int passportCounter) {
        this.passportCounter = passportCounter;
    }

    public int getProxyCounter() { return proxyCounter;}

    public void setProxyCounter(int proxyCounter) {
        this.proxyCounter = proxyCounter;
    }

    public int getDaneshjooCounter() { return this.daneshjooCounter;}

    public void setDaneshjooCounter(int daneshjooCounter) { this.daneshjooCounter = daneshjooCounter; }

    public int getVisaCounter() { return visaCounter;}

    public void setVisaCounter(int visaCounter) {
        this.visaCounter = visaCounter;
    }
    public void incrementPass(){
        passportCounter ++;
    }
    public void incrementVisa(){ visaCounter ++;}
    public void incrementProxy(){
        proxyCounter ++;
    }
    public void incrementDaneshjoo(){
        daneshjooCounter++;
    }

    @Override
    public boolean equals(Object obj) {
        Calendar thisDate = Calendar.getInstance();
        Calendar comparedDate = Calendar.getInstance();
        comparedDate.setTime(new Date(((Turn)obj).getDate()));
        thisDate.setTime(new Date(this.date));
        return thisDate.get(Calendar.DAY_OF_MONTH) == comparedDate.get(Calendar.DAY_OF_MONTH)
                && thisDate.get(Calendar.YEAR) == comparedDate.get(Calendar.YEAR)
                && thisDate.get(Calendar.MONTH) == comparedDate.get(Calendar.MONTH);
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String description = "\npassport -> "+ (passportCounter)
                + "\nproxy -> " + (proxyCounter)+ "\n visa -> " + (visaCounter) + "\nstudent -> " + (daneshjooCounter);
        return "The turn on " + simpleDateFormat.format(new Date(this.getDate())) + description;
    }
}
