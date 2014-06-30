package com.example.SystemHealth_Android;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by emou on 6/27/14.
 */
public class Contact implements Parcelable {
    String mName;
    int mAmount = 0;

    Contact(String name){
        mName = name;
    }
    Contact(String name,int amount){
        mName = name;
        mAmount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}