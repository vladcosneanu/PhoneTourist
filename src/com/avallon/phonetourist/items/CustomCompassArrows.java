package com.avallon.phonetourist.items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.interfaces.CustomButtonAnimationListener;

public class CustomCompassArrows extends View {
    
    private int width;
    private int height;
    private Path path6 = new Path();
    private Paint paint6 = new Paint();
    private Path path7 = new Path();
    private Paint paint7 = new Paint();
    private Path path8 = new Path();
    private Path path9 = new Path();
    private Paint textPaint = new Paint();
    private CustomButtonAnimationListener listener;

    public CustomCompassArrows(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        paint6.setAntiAlias(true);
        paint6.setColor(getContext().getResources().getColor(R.color.holo_red_light));
        paint6.setStyle(Paint.Style.FILL_AND_STROKE);
        paint6.setStrokeJoin(Paint.Join.ROUND);
        
        paint7.setAntiAlias(true);
        paint7.setColor(getContext().getResources().getColor(R.color.holo_blue_light));
        paint7.setStyle(Paint.Style.FILL_AND_STROKE);
        paint7.setStrokeJoin(Paint.Join.ROUND);
        
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Align.CENTER);
        textPaint.setTextSize(width * 20 / 100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int margin = width * 18 / 100;
        float textSize = width * 11 / 100;
        
        // add cardinal arrows
        margin = width * 8 / 100;
        path6.addArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin), -99, 18);
        path6.lineTo(width / 2, Math.round(width * 0.5 / 100));
        canvas.drawPath(path6, paint6);

        textSize = width * 4 / 100;
        textPaint.setTextSize(textSize);
        canvas.drawText("N", width / 2, width * 7 / 100, textPaint);

        path7.addArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin), -3, 6);
        path7.lineTo(width - Math.round(width * 4 / 100), height / 2);
        canvas.drawPath(path7, paint7);

        path8.addArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin), 85, 10);
        path8.lineTo(width / 2, height - Math.round(width * 3 / 100));
        canvas.drawPath(path8, paint7);

        textSize = width * 3 / 100;
        textPaint.setTextSize(textSize);
        canvas.drawText("S", width / 2, height - (width * 5 / 100), textPaint);

        path9.addArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin), 177, 6);
        path9.lineTo(Math.round(width * 4 / 100), height / 2);
        canvas.drawPath(path9, paint7);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(width, height);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
