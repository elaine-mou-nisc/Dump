package com.example.SystemHealth_Android;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by emou on 7/3/14.
 */
public class Contact implements Parcelable {

    public Contact(String name){
        mName = name;
    }

    String mName;
    int mContactId;
    long mDateCreated;
    String mStatus;
    int mRequestCode;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
