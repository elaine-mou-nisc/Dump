package com.example.SystemHealth_Android;

import android.app.Activity;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Enumeration for user choices for date selection
 *
 * Created by emou on 7/14/14.
 */
public enum DateOptions {
    DAY("Last Day"), WEEK("Last Week"),THIRTY_DAYS("Last 30 Days"),SIXTY_DAYS("Last 60 Days"),NINETY_DAYS("Last 90 Days"),
    YEAR("Last Year"),CUSTOM("Custom Date Range");

    private String description;

    DateOptions(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    @Override
    public String toString(){
        return description;
    }
}
