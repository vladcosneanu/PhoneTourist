package com.avallon.phonetourist.items;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.interfaces.CustomButtonAnimationListener;

public class CustomButton extends View {

    private Paint paint = new Paint();
    private Path path = new Path();
    private Paint paint2 = new Paint();
    private Path path2 = new Path();
    private Paint paint3 = new Paint();
    private Path path3 = new Path();
    private Paint paint4 = new Paint();
    private Path path4 = new Path();
    private Paint paint5 = new Paint();
    private Path path5 = new Path();
    private Paint paint6 = new Paint();
    private Path path6 = new Path();
    private Paint paint7 = new Paint();
    private Path path7 = new Path();
    private Path path8 = new Path();
    private Path path9 = new Path();
    private Paint textPaint = new Paint();
    private Camera camera = new Camera();
    private Matrix mMatrix = new Matrix();;
    private int width;
    private int height;
    private float previousAngle = 0;
    private float previousRotationAngle = 0;
    private float previousColorProportion = 0;
    private Timer timer;
    private CustomButtonAnimationListener listener;
    private boolean buttonPressed = false;
    private int colorInterpolated = 0;
    private int colorFrom = R.string.holo_blue_light;
    private int colorTo = R.string.holo_green_light;
    private float bearing = 0;

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setColor(getContext().getResources().getColor(R.color.holo_blue_light));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        paint2.setAntiAlias(true);
        paint2.setColor(getContext().getResources().getColor(R.color.holo_red_light));
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setStrokeJoin(Paint.Join.ROUND);

        paint3.setAntiAlias(true);
        paint3.setColor(getContext().getResources().getColor(R.color.holo_orange_light));
        paint3.setStyle(Paint.Style.FILL_AND_STROKE);
        paint3.setStrokeJoin(Paint.Join.ROUND);

        paint4.setAntiAlias(true);
        paint4.setColor(getContext().getResources().getColor(R.color.holo_green_light));
        paint4.setStyle(Paint.Style.FILL_AND_STROKE);
        paint4.setStrokeJoin(Paint.Join.ROUND);

        paint5.setAntiAlias(true);
        paint5.setColor(getContext().getResources().getColor(R.color.holo_purple_light));
        paint5.setStyle(Paint.Style.FILL_AND_STROKE);
        paint5.setStrokeJoin(Paint.Join.ROUND);

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
        paint.setStyle(Paint.Style.FILL);
        int margin = width * 18 / 100;

        float textSize = width * 11 / 100;
        textPaint.setTextSize(textSize);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        camera.save();
        camera.rotate(0, previousRotationAngle, 0);
        camera.getMatrix(mMatrix);
        mMatrix.preTranslate(-getWidth() / 2, -getHeight() / 2);
        mMatrix.postTranslate(getWidth() / 2, getHeight() / 2);
        canvas.concat(mMatrix);
        canvas.drawCircle(width / 2, height / 2, height / 2 - margin, paint);

        camera.rotate(0, previousRotationAngle - 90, 0);
        canvas.concat(mMatrix);
        if (previousRotationAngle <= 90) {
            canvas.drawText(getContext().getString(R.string.start), width / 2, height / 2 - textSize / 4, textPaint);
            canvas.drawText(getContext().getString(R.string.tour), width / 2, height / 2 + textSize, textPaint);
        } else {
            canvas.drawText(getContext().getString(R.string.loading), width / 2, height / 2 + textSize / 3, textPaint);
        }
        canvas.restore();
        camera.restore();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width * 1 / 100);

        margin = width * 10 / 100;
        canvas.drawArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin), 0, 360,
                false, paint);

        margin = width * 12 / 100;
        canvas.drawArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin), 0, 360,
                false, paint);

        margin = width * 14 / 100;
        canvas.drawArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin), 0, 360,
                false, paint);

        margin = width * 16 / 100;
        canvas.drawArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin), 0, 360,
                false, paint);

        paint2.setStrokeWidth(width * 1 / 100);
        paint3.setStrokeWidth(width * 1 / 100);
        paint4.setStrokeWidth(width * 1 / 100);
        paint5.setStrokeWidth(width * 1 / 100);
        canvas.drawPath(path2, paint2);
        canvas.drawPath(path3, paint3);
        canvas.drawPath(path4, paint4);
        canvas.drawPath(path5, paint5);
        canvas.drawPath(path, paint);

        if (!buttonPressed) {
            // add cardinal arrows
            margin = width * 8 / 100;
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(-bearing, width / 2, height / 2);
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
    
            canvas.restore();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(width, height);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (!buttonPressed) {
                path.rewind();
                path2.rewind();
                path3.rewind();
                path4.rewind();
                path5.rewind();
                previousAngle = 0;

                if (timer != null) {
                    timer.cancel();
                }

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int margin = width * 10 / 100;
                        path2.addArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height
                                - margin), -90 + previousAngle, 3);

                        margin = width * 12 / 100;
                        path3.addArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height
                                - margin), (-1) * previousAngle, -3);

                        margin = width * 14 / 100;
                        path4.addArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height
                                - margin), 90 + previousAngle, 3);

                        margin = width * 16 / 100;
                        path5.addArc(new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height
                                - margin), (-1) * (180 + previousAngle), -3);

                        previousAngle += 3;
                        if (previousAngle == 360) {
                            timer.cancel();
                            buttonPressed = true;
                            listener.onCustomButtonAnimationEnd();
                            animateColor();
                        }
                        postInvalidate();
                    }
                }, 0, 5);
            }
            return true;
        case MotionEvent.ACTION_MOVE:
            // path.lineTo(eventX, eventY);
            break;
        case MotionEvent.ACTION_UP:
            if (!buttonPressed) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int margin = width * 10 / 100;
                        path.addArc(
                                new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin),
                                -90 + previousAngle, -3);

                        margin = width * 12 / 100;
                        path.addArc(
                                new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin),
                                (-1) * previousAngle, 3);

                        margin = width * 14 / 100;
                        path.addArc(
                                new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin),
                                90 + previousAngle, -3);

                        margin = width * 16 / 100;
                        path.addArc(
                                new RectF((width - height) / 2 + margin, margin, width - (width - height) / 2 - margin, height - margin),
                                (-1) * (180 + previousAngle), 3);

                        previousAngle -= 3;
                        if (previousAngle == 0) {
                            path2.rewind();
                            path3.rewind();
                            path4.rewind();
                            path5.rewind();
                            timer.cancel();
                        }
                        postInvalidate();
                    }
                }, 0, 5);
            }
            return true;
        default:
            return false;
        }

        return true;
    }

    public CustomButtonAnimationListener getCustomButtonAnimationListener() {
        return listener;
    }

    public void setCustomButtonAnimationListener(CustomButtonAnimationListener listener) {
        this.listener = listener;
    }

    private void animateColor() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (previousRotationAngle < 180) {
                    previousRotationAngle += 2;
                }

                colorInterpolated = interpolateColor(colorFrom, colorTo, previousColorProportion);
                if (previousColorProportion >= 1) {
                    previousColorProportion = 0.003f;

                    if (colorInterpolated == getContext().getResources().getColor(R.color.holo_blue_light)) {
                        // blue -> green
                        colorFrom = R.string.holo_blue_light;
                        colorTo = R.string.holo_green_light;
                    }

                    if (colorInterpolated == getContext().getResources().getColor(R.color.holo_green_light)) {
                        // green -> purple
                        colorFrom = R.string.holo_green_light;
                        colorTo = R.string.holo_purple_light;
                    }

                    if (colorInterpolated == getContext().getResources().getColor(R.color.holo_purple_light)) {
                        // purple -> red
                        colorFrom = R.string.holo_purple_light;
                        colorTo = R.string.holo_red_light;
                    }

                    if (colorInterpolated == getContext().getResources().getColor(R.color.holo_red_light)) {
                        // red -> orange
                        colorFrom = R.string.holo_red_light;
                        colorTo = R.string.holo_orange_light;
                    }

                    if (colorInterpolated == getContext().getResources().getColor(R.color.holo_orange_light)) {
                        // orange -> blue
                        colorFrom = R.string.holo_orange_light;
                        colorTo = R.string.holo_blue_light;
                    }
                }

                previousColorProportion += 0.003f;

                paint.setColor(colorInterpolated);
                postInvalidate();
            }
        }, 0, 5);
    }

    private int interpolateColor(int resourceA, int resourceB, float proportion) {
        int colorA = (int) Long.parseLong(getResources().getString(resourceA), 16);
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = (colorA >> 0) & 0xFF;

        int colorB = (int) Long.parseLong(getResources().getString(resourceB), 16);
        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = (colorB >> 0) & 0xFF;

        int r = interpolate(rA, rB, proportion);
        int g = interpolate(gA, gB, proportion);
        int b = interpolate(bA, bB, proportion);

        return Color.rgb(r, g, b);
    }

    private int interpolate(int a, int b, float proportion) {
        return Math.round((a + ((b - a) * proportion)));
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
        invalidate();
//        float difference = bearing - this.bearing;
//        if (difference > 5) {
//            this.bearing = bearing - (difference / 100);
//            invalidate();
//        } else if (difference < -5) {
//            this.bearing = bearing + (difference / 100);
//            invalidate();
//        }
    }
}
