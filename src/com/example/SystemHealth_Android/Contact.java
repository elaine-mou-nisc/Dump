package com.example.SystemHealth_Android;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data type associated with Contact Tracking contacts
 *
 * Created by emou on 7/3/14.
 */
public class Contact implements Parcelable {

    public Contact(String name){
        mName = name;
    }

    String mName;//person's name
    int mContactId;//id number
    long mDateCreated;//date of creation
    String mStatus;//"open" or "closed"; currently represented by "O" or "C"
    int mRequestCode;//what kind of request the contact is associated with

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
