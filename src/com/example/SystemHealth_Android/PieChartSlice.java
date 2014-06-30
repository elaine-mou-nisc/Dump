package com.example.SystemHealth_Android;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by emou on 6/19/14.
 */
public class PieChartSlice implements Parcelable{

    float mValue;
    int mColor;
    String mSubject;

    PieChartSlice(float value, int color){
        mValue = value;
        mColor = color;
    }

    PieChartSlice(float value){
        mValue = value;
        mColor = Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
