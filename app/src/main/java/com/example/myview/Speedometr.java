package com.example.myview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Speedometr extends View {

    public static final Paint BACKGROUND_ARC_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    public static final Paint ARROW_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    public static final Paint CIRCLE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float STROKE_WIDTH = 50f;
    private static final Paint TEXT_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG) ;
    private static final Paint LITTLE_TEXT_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG) ;
    private static float LITTLE_RADIUS = 40f;
    private static float MICRO_RADIUS = 20f;
    private static final float RADIUS = 300f;
    private static int MAX_SPEED;
    private static final int MAX_SPEED_DEFAULT = 200;
    private static int SPEED;
    private static final RectF ARC_RECT = new RectF(STROKE_WIDTH / 2, STROKE_WIDTH / 2, 2 * RADIUS, 2 * RADIUS);
    private static int progress;
    private static Path farthest = new Path();
    private static final Rect mTextBounds = new Rect();
    private static float circleCenter = RADIUS + STROKE_WIDTH / 4;
    private int COLOR_LOW = Color.GREEN;
    private int COLOR_MEDIUM = Color.YELLOW;
    private int COLOR_HIGH = Color.RED;
    private int ARROW_COLOR = Color.BLACK;
    private static final float ARROW_WIDTH = 12f;
    private static final float TEXT_SIZE_BIG = 80f;
    private static final float TEXT_SIZE_LITTLE = TEXT_SIZE_BIG/2;

    public static void setMaxSpeed(int maxSpeed) { MAX_SPEED = maxSpeed; }
    public static void setSPEED(int SPEED) { Speedometr.SPEED = SPEED; }
    public void setColorLow(int colorLow) { COLOR_LOW = colorLow; }
    public void setColorMedium(int colorMedium) { COLOR_MEDIUM = colorMedium; }
    public void setColorHigh(int colorHigh) { COLOR_HIGH = colorHigh; }
    public void setArrowColor(int arrowColor) { ARROW_COLOR = arrowColor; }


    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public Speedometr(Context context) {
        this(context, null, 0);
    }

    public Speedometr(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Speedometr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        farthest.reset();
        float x = (float) Math.cos(-13 * Math.PI / 6 + 4*Math.PI * progress / (3*MAX_SPEED_DEFAULT));
        float y = (float) Math.sin(-13 * Math.PI / 6 + 4*Math.PI * progress / (3*MAX_SPEED_DEFAULT));
        float x1 = (float) Math.cos(Math.PI / 3 + 4*Math.PI * progress / (3*MAX_SPEED_DEFAULT));
        float y1 = (float) Math.sin(Math.PI / 3 + 4*Math.PI * progress / (3*MAX_SPEED_DEFAULT));
        float x2 = (float) Math.cos(-2 * Math.PI / 3 + 4*Math.PI * progress / (3*MAX_SPEED_DEFAULT));
        float y2 = (float) Math.sin(-2 * Math.PI / 3 + 4*Math.PI * progress / (3*MAX_SPEED_DEFAULT));

        canvas.drawArc(ARC_RECT, -210, 240, false, BACKGROUND_ARC_PAINT);
        canvas.drawCircle(circleCenter, circleCenter, LITTLE_RADIUS, CIRCLE_PAINT);
        farthest.moveTo((1-x1)*MICRO_RADIUS + RADIUS-MICRO_RADIUS/2.5f,(1-y1)*MICRO_RADIUS  + RADIUS-MICRO_RADIUS /2.5f);
        farthest.lineTo((1-x2)*MICRO_RADIUS + RADIUS-MICRO_RADIUS/2.5f,(1-y2)*MICRO_RADIUS  + RADIUS-MICRO_RADIUS /2.5f);
        farthest.lineTo((1 - x) * (circleCenter), (1 - y) * (circleCenter));
        farthest.lineTo((1-x1)*MICRO_RADIUS + RADIUS-MICRO_RADIUS/2.5f,(1-y1)*MICRO_RADIUS  + RADIUS-MICRO_RADIUS /2.5f);
        canvas.drawPath(farthest,ARROW_PAINT);
        drawText(canvas);

    }

    private void setSpeed(){
        progress = SPEED/(MAX_SPEED/MAX_SPEED_DEFAULT);
    }

    private void drawText(Canvas canvas) {
        int speed = (int) progress*(MAX_SPEED/MAX_SPEED_DEFAULT);
        final String progressString = formatString(speed);
        getTextBounds(progressString);
        float x = ARC_RECT.width() / 2f - mTextBounds.width() / 2f - mTextBounds.left + ARC_RECT.left;
        float y = ARC_RECT.height()/1.4f + mTextBounds.height() / 2f - mTextBounds.bottom + ARC_RECT.top;
        canvas.drawText(progressString, x, y, TEXT_PAINT);
        canvas.drawText(getContext().getString(R.string.min_speed), 30f , ARC_RECT.height()-50f, LITTLE_TEXT_PAINT);
        canvas.drawText(MAX_SPEED +" km/h", ARC_RECT.width()-120f, ARC_RECT.height()-50f , LITTLE_TEXT_PAINT);

    }

    private String formatString(float progress) {
        return String.format("%.0f km/h", progress);
    }

    private void getTextBounds(@NonNull String progressString) {
        TEXT_PAINT.getTextBounds(progressString, 0, progressString.length(), mTextBounds);
    }

    private void extractAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            final Resources.Theme theme = context.getTheme();
            final TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.SpeedometrView, 0, R.style.SpeedometrViewDefault);
            try {
                SPEED = typedArray.getInt(R.styleable.SpeedometrView_speed, 0);
                MAX_SPEED = typedArray.getInt(R.styleable.SpeedometrView_max_speed, 200);
                COLOR_LOW = typedArray.getColor(R.styleable.SpeedometrView_low_speed_color, Color.GREEN);
                COLOR_MEDIUM = typedArray.getColor(R.styleable.SpeedometrView_medium_speed_color, Color.YELLOW);
                COLOR_HIGH = typedArray.getColor(R.styleable.SpeedometrView_high_speed_color, Color.RED);
                ARROW_COLOR = typedArray.getColor(R.styleable.SpeedometrView_arrow_color, Color.BLACK);
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        extractAttributes(context, attrs);
        configureBackground();
        configureArrow();
        configureCircle(CIRCLE_PAINT, Color.BLACK, Paint.Style.FILL);
        configureText(Color.BLACK, Paint.Style.FILL, TEXT_SIZE_BIG, TEXT_PAINT);
        configureText(Color.BLACK, Paint.Style.FILL, TEXT_SIZE_LITTLE, LITTLE_TEXT_PAINT);
        setSpeed();
    }

    private void configureArrow() {
        ARROW_PAINT.setColor(ARROW_COLOR);
        ARROW_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        ARROW_PAINT.setStrokeWidth(ARROW_WIDTH);
    }

    private void configureCircle(Paint circlePaint, int color, Paint.Style fill) {
        circlePaint.setColor(color);
        circlePaint.setStyle(fill);
    }

    private void configureText(int color, Paint.Style style, float textSize, Paint paint) {
        paint.setColor(color);
        paint.setStyle(style);
        paint.setTextSize(textSize);
    }

    private void configureBackground() {
        int[] gradientColors = new int[3];
        gradientColors[0] = COLOR_LOW;
        gradientColors[1] = COLOR_MEDIUM;
        gradientColors[2] = COLOR_HIGH;

        LinearGradient linearGradient = new LinearGradient(0, ARC_RECT.top, 2 * RADIUS, getHeight(),
                gradientColors, null, Shader.TileMode.CLAMP);

        BACKGROUND_ARC_PAINT.setShader(linearGradient);
        BACKGROUND_ARC_PAINT.setStyle(Paint.Style.STROKE);
        BACKGROUND_ARC_PAINT.setStrokeWidth(STROKE_WIDTH);
    }
}
