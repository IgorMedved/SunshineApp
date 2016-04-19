package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Admin User on 1/6/2016.
 * provides a small view for showing wind direction information on a compass
 */

// source code adapted from http://stackoverflow.com/questions/15240865/how-to-make-a-customized-compass-view-in-android
public class MyCompassView extends View {

    private Paint paint;
    private double position = 0;

    public MyCompassView(Context context)
    {
        super(context);

    }

    public MyCompassView(Context context, AttributeSet attributes)
    {
        super(context, attributes);
    }

    public MyCompassView(Context context, AttributeSet attributes, int defaultStyle)
    {
        super(context, attributes, defaultStyle);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = hSpecSize;

        if(hSpecMode == MeasureSpec.EXACTLY)
            myHeight = hSpecSize;
        else if (hSpecMode==MeasureSpec.AT_MOST)
        {
            myHeight = 100;
        }

        int wSpecMode= MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = wSpecSize;

        if(wSpecMode == MeasureSpec.EXACTLY)
            myWidth = wSpecSize;
        else if (hSpecMode==MeasureSpec.AT_MOST)
        {
            myWidth = 100;
        }

        setMeasuredDimension(myWidth, myHeight);

    }

    private void init()
    {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setTextSize(25);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (paint==null)
            init();

        int xPoint = getMeasuredWidth() / 2;
        int yPoint = getMeasuredHeight() / 2;

        float radius = (float) (Math.max(xPoint, yPoint) * 0.4);
        if (canvas!=null && paint!=null) {
            canvas.drawCircle(xPoint, yPoint, radius, paint);
            //canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

            // 3.143 is a good approximation for the circle
            canvas.drawLine(xPoint,
                    yPoint,
                    (float) (xPoint + radius
                            * Math.sin((double) (-position) / 180 * 3.143)),
                    (float) (yPoint - radius
                            * Math.cos((double) (-position) / 180 * 3.143)), paint);


            int margin = 5;


            canvas.drawText("S", xPoint - paint.measureText("S")/2, yPoint+ radius+ paint.getTextSize(), paint); // south
            canvas.drawText("N", xPoint - paint.measureText("N")/2, yPoint - radius - margin, paint ); //south
            canvas.drawText("E", xPoint - radius - margin - paint.measureText("E"), yPoint+paint.getTextSize()/3, paint);
            canvas.drawText("W", xPoint + radius + margin, yPoint+paint.getTextSize()/3, paint);

        }
    }

    public void updateData(double position)
    {
        this.position = position;
        invalidate();
    }

}