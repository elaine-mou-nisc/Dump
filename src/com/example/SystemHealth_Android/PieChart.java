package com.example.SystemHealth_Android;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by emou on 6/19/14.
 */
public class PieChart extends View {

    ArrayList<PieChartSlice> sliceArrayList;

    private ArrayList<Paint> paintArray;
    private ArrayList<Path> pathArray;

    RectF oval = new RectF();

    private float centerX;
    private float centerY;
    private float radius;

    public void setSliceArrayList(ArrayList<PieChartSlice> inSliceList){
        sliceArrayList = inSliceList;

        paintArray = new ArrayList<Paint>(sliceArrayList.size());
        pathArray = new ArrayList<Path>(sliceArrayList.size());
        initializePaintAndPath();
        invalidate();
    }

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    private void initializePaintAndPath(){
        for(int i=0;i<sliceArrayList.size();i++){
            Paint paint = new Paint();
            paintArray.add(i,paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(sliceArrayList.get(i).mColor);
            paint.setAntiAlias(true);

            Path path = new Path();
            pathArray.add(i,path);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        // Setting up the oval area in which the arc will be drawn
        if (width > height){
            radius = height/2;
        }else{
            radius = width/2;
        }
        oval.set(centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenDimension = Math.min(widthSize, heightSize);
        centerX = chosenDimension / 2;
        centerY = chosenDimension / 2;
        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    @Override
    public void onDraw(Canvas canvas) {

        float total = 0.0f;
        for(int i=0;i<sliceArrayList.size();i++){
            total += sliceArrayList.get(i).mValue;
        }
        float currentValue = 0.0f;

        int startAngle = 0;
        for(int i=0;i<sliceArrayList.size();i++){
            startAngle = (int) (currentValue/total * 360);
            int arcAngle = (int) (sliceArrayList.get(i).mValue/total * 360);

            canvas.drawArc(oval,startAngle,arcAngle,true,paintArray.get(i));

            currentValue += sliceArrayList.get(i).mValue;
        }
    }
}
