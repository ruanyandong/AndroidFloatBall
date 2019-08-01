package leavesc.hello.floatball.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import leavesc.hello.floatball.R;


public class CircleTextView extends AppCompatTextView {
    private Paint circlePaint;
    private Paint backPaint;
    private Paint textPaint;
    private int storkColor = Color.WHITE;
    private int circleBackColor = Color.WHITE;
    private float storkWidth;


    public CircleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setStyle(Paint.Style.FILL);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        storkWidth = 0;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);
            storkColor = typedArray.getColor(R.styleable.CircleTextView_storkColor, storkColor);
            circleBackColor = typedArray.getColor(R.styleable.CircleTextView_backColor, circleBackColor);
            storkWidth = typedArray.getDimension(R.styleable.CircleTextView_storkWidth, storkWidth);
            typedArray.recycle();
        }
        if (storkWidth != 0) {
            circlePaint.setStrokeWidth(storkWidth);
            circlePaint.setColor(storkColor);
        }
        backPaint.setColor(circleBackColor);
        textPaint.setColor(getCurrentTextColor());
        textPaint.setTextSize(getTextSize());
    }

    public CircleTextView(Context context) {
        this(context, null);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int radius;
        int storkRadius;
        int textWidth = (int) textPaint.measureText(getText().toString());
        if (width > height) {
            if (height > textWidth) {
                radius = height;
            } else {
                setHeight(textWidth + getPaddingTop() + getPaddingBottom());
                radius = textWidth;
            }
        } else {
            if (width > textWidth) {
                radius = width;
            } else {
                setWidth(textWidth + getPaddingRight() + getPaddingLeft());
                radius = textWidth;
            }
        }
        storkRadius= (int) (radius/2-storkWidth);
        radius= storkRadius-1;
        if (storkWidth != 0)
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, storkRadius, circlePaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, backPaint);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        canvas.drawText(getText().toString(), getWidth() / 2 - textPaint.measureText(getText().toString()) / 2, getHeight() / 2 - fontMetrics.descent + (fontMetrics.bottom - fontMetrics.top) / 2, textPaint);

    }

    public void setMyStorkColor(@ColorInt int color) {
        this.storkColor = color;
        circlePaint.setColor(storkColor);
        invalidate();
    }

    public void setBackColor(@ColorInt int color) {
        this.circleBackColor = color;
        backPaint.setColor(circleBackColor);
        invalidate();
    }

    public void setMyTextColor(@ColorInt int color) {
        textPaint.setColor(color);
        invalidate();
    }
}
