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

    Request(String name){
        mDescription = name;
    }
    Request(String name, int amount){
        mDescription = name;
        mCount = amount;
    }

    public static final Parcelable.Creator<Request> CREATOR = new Creator<Request>(){
        @Override
        public Request createFromParcel(Parcel source) {
            return new Request(source);
        }
        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public Request(Parcel source) {
        readFromParcel(source);
    }

    private void readFromParcel(Parcel source) {
        mDescription = source.readString();
        mCode = source.readInt();
        mCount = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDescription);
        dest.writeInt(mCode);
        dest.writeInt(mCount);
    }
}