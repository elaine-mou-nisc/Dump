package com.example.SystemHealth_Android;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by emou on 6/27/14.
 */
public class Request implements Parcelable {
    int mCode = 0;
    String mDescription;
    int mCount = 0;
    ArrayList<Contact> mContacts;

    Request(String name){
        mDescription = name;
    }
    Request(String name, int amount){
        mDescription = name;
        mCount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}