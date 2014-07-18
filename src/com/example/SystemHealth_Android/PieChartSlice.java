package com.example.SystemHealth_Android;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data type for pie chart data item
 * Contains a name, decimal value for representation, and color to be shown
 *
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

    public static final Parcelable.Creator<PieChartSlice> CREATOR = new Parcelable.Creator<PieChartSlice>(){
        @Override
        public PieChartSlice createFromParcel(Parcel in){
          return new PieChartSlice(in);
        }

        @Override
        public PieChartSlice[] newArray(int size) {
            return new PieChartSlice[size];
        }
    };

    public PieChartSlice(Parcel in) {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        mValue = in.readFloat();
        mColor = in.readInt();
        mSubject = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSubject);
        dest.writeInt(mColor);
        dest.writeFloat(mValue);
    }
}
