package info.jackrex.doubleseekbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;

/**
 * @author Jackrex
 */
public class DoubleSeekView extends View {

    private static final int CLICK_ON_LOW = 1;
    private static final int CLICK_ON_HIGH = 2;
    private static final int CLICK_IN_LOW_AREA = 3;
    private static final int CLICK_IN_HIGH_AREA = 4;
    private static final int CLICK_INVAILD = 0;
    private static final int CLICK_MIDDLE = 6;

    private static final int[] STATE_NORMAL = {};
    private static final int[] STATE_PRESSED = {android.R.attr.state_pressed,
            android.R.attr.state_window_focused,};
    int mScrollBarHeight = 0;
    float lowOffset = 0;
    float highOffset = 0;
    private Drawable mScrollBarBgNormal;
    private Drawable mScrollBarProgressed;
    private Drawable mThumbLow;
    private Drawable mThumbHigh;
    private Drawable mKedu;
    private int mScollBarWidth;
    private int mThumbWidth;
    private int mThumbHeight;
    private int mMiddleLength = 0;
    private int mOffsetLow = 0;
    private int mOffsetHigh = mOffsetLow + mMiddleLength;
    private int mOffsetLowInit;
    private Bitmap bitmap;
    private boolean isMiddle = false;
    private float mMin = 0;
    private float mMax = 0;
    private int mFlag = CLICK_INVAILD;
    private OnSeekBarChangeListener mBarChangeListener;
    private int preOil = 0;
    private int aftOil = 100;
    private int padding = 0;
    private int total;
    private int slide_title_width;

    public DoubleSeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public DoubleSeekView(Context context) {
        // TODO Auto-generated constructor stub
        this(context, null);
    }

    public DoubleSeekView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.SeekBarPressure, defStyle, 0);
        mMin = array.getFloat(R.styleable.SeekBarPressure_minValue, mMin);
        mMax = array.getFloat(R.styleable.SeekBarPressure_maxValue, mMax);
        mScollBarWidth = array.getLayoutDimension(
                R.styleable.SeekBarPressure_width, "layout_wight");
        array.recycle();

        mScollBarWidth = mKedu.getIntrinsicWidth();
        padding = padding + mThumbWidth / 2;

        mOffsetLow = mThumbWidth / 6; // 初始值 不为0 padding -mThumbWidth/3

        mOffsetLowInit = mOffsetLow;

        mOffsetHigh = mOffsetLow + mMiddleLength;
        total = mScollBarWidth - mThumbWidth / 3;
        aftOil = (int) (formatFloat((float) (mOffsetHigh - mOffsetLowInit)
                / total) * 100);
    }

    public void setMiddleLength(int mMiddleLength) {
        mOffsetHigh = mOffsetLow + mMiddleLength;
        if (mOffsetHigh >= mScollBarWidth - mThumbWidth / 6) {
            mOffsetHigh = mScollBarWidth - mThumbWidth / 6;
            mOffsetLow = mOffsetLowInit;
        }
        aftOil = (int) (formatFloat((float) (mOffsetHigh - mOffsetLowInit)
                / total) * 100);
        preOil = (int) (formatFloat((float) (mOffsetLow - mOffsetLowInit)
                / total) * 100);
        this.mMiddleLength = mOffsetHigh - mOffsetLow;
        invalidate();
    }

    public int getTotal() {
        return total;
    }

    private void init(Context context) {
        // TODO Auto-generated method stub
        Resources res = getResources();
        mScrollBarBgNormal = res.getDrawable(R.drawable.slide_bg_white);
        mScrollBarProgressed = res.getDrawable(R.drawable.slide_bg);

        mScrollBarHeight = mScrollBarBgNormal.getIntrinsicHeight();

        BitmapDrawable progressedDrawable = new BitmapDrawable(BitmapFactory.decodeResource(
                getResources(), R.drawable.slide_title));
        progressedDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
        progressedDrawable.setDither(true);

        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.slide_title);
        slide_title_width = res.getDrawable(R.drawable.slide_title)
                .getIntrinsicWidth();

        mKedu = res.getDrawable(R.drawable.kedu);
        mThumbLow = res.getDrawable(R.drawable.slide_up);
        mThumbHigh = res.getDrawable(R.drawable.slide_down);
        mThumbWidth = mThumbLow.getIntrinsicWidth();
        mThumbHeight = mThumbLow.getIntrinsicHeight() / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mScollBarWidth + mThumbWidth;
        int height = mThumbHeight;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        int thumbHeight = 0;
        int thumbBottom = thumbHeight + mThumbHeight;

        int progressHeight = mThumbHeight / 2 - 15;
        int progressBottom = progressHeight + mScrollBarHeight;

        int textTop1Height = thumbHeight + ResolutionUtil
                .dip2px(getContext(), Float.valueOf(20));// 加油前
        int textTop2Height = thumbHeight + ResolutionUtil
                .dip2px(getContext(), Float.valueOf(45));// 32%
        int textBot1Height = thumbBottom - ResolutionUtil
                .dip2px(getContext(), Float.valueOf(10));// 76%
        int textBot2Height = thumbBottom - ResolutionUtil
                .dip2px(getContext(), Float.valueOf(35));// 加油后

        mScrollBarBgNormal.setBounds(padding, progressHeight, mScollBarWidth
                + padding, progressBottom);
        mScrollBarBgNormal.draw(canvas);

        mScrollBarProgressed.setBounds(padding, progressHeight, padding
                + mOffsetLow, progressBottom);
        mScrollBarProgressed.draw(canvas);

        int count = (mMiddleLength + mThumbWidth) / slide_title_width - 3;

        for (int i = 0; i < count; i++) {

            canvas.drawBitmap(bitmap, mOffsetLow + mThumbWidth / 2 + i
                    * slide_title_width, progressHeight, null);
        }

        mKedu.setBounds(padding, progressBottom, mScollBarWidth + padding,
                progressBottom + mKedu.getIntrinsicHeight());
        mKedu.draw(canvas);

        mThumbLow.setBounds(mOffsetLow, thumbHeight, mOffsetLow + mThumbWidth,
                thumbBottom);
        mThumbHigh.setBounds(mOffsetHigh, thumbHeight, mOffsetHigh
                + mThumbWidth, thumbBottom);

        mThumbLow.draw(canvas);
//        mThumbHigh.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#B5B5B5"));
        paint.setStrokeWidth(3);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(ResolutionUtil.dip2px(getContext(), Float.valueOf(15)));

        Paint paintNum = new Paint();
        paintNum.setColor(Color.parseColor("#379997"));
        paintNum.setStrokeWidth(3);
        paintNum.setTextAlign(Align.CENTER);
        paintNum.setTextSize(ResolutionUtil.dip2px(getContext(), Float.valueOf(20)));

//        canvas.drawText("加油前", mOffsetLow + mThumbWidth / 2, textTop1Height,
//                paint);
//        canvas.drawText("加油后", mOffsetLow + mMiddleLength + mThumbWidth / 2,
//                textBot2Height, paint);

        canvas.drawText(String.valueOf(preOil), mOffsetLow + mThumbWidth / 2,
                (progressHeight + progressBottom) / 2, paintNum);
//        canvas.drawText(aftOil + "%", mOffsetLow + mMiddleLength + mThumbWidth
//                / 2, textBot1Height, paintNum);

        if (mBarChangeListener != null) {
            mBarChangeListener.onProgressChanged(this, preOil, aftOil);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        mFlag = getAreaFlag(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mFlag == CLICK_ON_LOW) {
                mThumbLow.setState(STATE_PRESSED);

            } else if (mFlag == CLICK_ON_HIGH) {
                mThumbHigh.setState(STATE_PRESSED);
            } else if (mFlag == CLICK_IN_LOW_AREA) {
                mThumbLow.setState(STATE_PRESSED);
                lowOffset = mOffsetLow - event.getX();
            } else if (mFlag == CLICK_IN_HIGH_AREA) {
                highOffset = event.getX() - mOffsetHigh;
            } else if (mFlag == CLICK_MIDDLE) {
                lowOffset = event.getX() - mOffsetLow;
                highOffset = mOffsetHigh - event.getX();
                isMiddle = true;
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mFlag == CLICK_MIDDLE) {

                if (isMiddle) {

                    int realLow = formatInt(event.getX() - lowOffset);
                    int realHigh = formatInt(event.getX() + highOffset);

                    if (realLow <= mOffsetLowInit) {
                        mOffsetLow = mOffsetLowInit;
                        mOffsetHigh = mOffsetLow + mMiddleLength;
                    } else if (realHigh >= mScollBarWidth - mThumbWidth / 6) {
                        mOffsetHigh = mScollBarWidth - mThumbWidth / 6;
                        mOffsetLow = mOffsetHigh - mMiddleLength;
                    } else {
                        mOffsetLow = realLow;
                        mOffsetHigh = realHigh;
                    }
                }

            } else if (mFlag == CLICK_ON_LOW) {

                mOffsetLow = formatInt(event.getX());
                mOffsetHigh = mOffsetLow + mMiddleLength;
                if (mOffsetLow <= mOffsetLowInit) {
                    mOffsetLow = mOffsetLowInit;
                    mOffsetHigh = mOffsetLow + mMiddleLength;
                }
                if (mOffsetHigh >= mScollBarWidth - mThumbWidth / 6) {
                    mOffsetHigh = mScollBarWidth - mThumbWidth / 6;
                    mOffsetLow = mOffsetHigh - mMiddleLength;
                }

            } else if (mFlag == CLICK_ON_HIGH) {

                mOffsetHigh = formatInt(event.getX());
                mOffsetLow = mOffsetHigh - mMiddleLength;
                if (mOffsetLow <= mOffsetLowInit) {
                    mOffsetLow = mOffsetLowInit;
                    mOffsetHigh = mOffsetLow + mMiddleLength;
                }

                if (mOffsetHigh >= mScollBarWidth - mThumbWidth / 6) {
                    mOffsetHigh = mScollBarWidth - mThumbWidth / 6;
                    mOffsetLow = mOffsetHigh - mMiddleLength;
                }

            } else if (mFlag == CLICK_IN_LOW_AREA) {

                int realLow = formatInt(event.getX() + lowOffset);
                int realHigh = realLow + mMiddleLength;

                if (realLow <= mOffsetLowInit) {
                    mOffsetLow = mOffsetLowInit;
                    mOffsetHigh = mOffsetLow + mMiddleLength;
                } else if (realHigh >= mScollBarWidth - mThumbWidth / 6) {
                    mOffsetHigh = mScollBarWidth - mThumbWidth / 6;
                    mOffsetLow = mOffsetHigh - mMiddleLength;
                } else {
                    mOffsetLow = realLow;
                    mOffsetHigh = realHigh;
                }
            } else if (mFlag == CLICK_IN_HIGH_AREA) {

                int realHigh = formatInt(event.getX() - highOffset);
                int realLow = realHigh - mMiddleLength;

                if (realLow <= mOffsetLowInit) {
                    mOffsetLow = mOffsetLowInit;
                    mOffsetHigh = mOffsetLow + mMiddleLength;
                } else if (realHigh >= mScollBarWidth - mThumbWidth / 6) {
                    mOffsetHigh = mScollBarWidth - mThumbWidth / 6;
                    mOffsetLow = mOffsetHigh - mMiddleLength;
                } else {
                    mOffsetLow = realLow;
                    mOffsetHigh = realHigh;
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mThumbLow.setState(STATE_NORMAL);
            mThumbHigh.setState(STATE_NORMAL);
            isMiddle = false;
        }
        resetProgress(mOffsetLow, mOffsetHigh);
        return true;
    }

    private void resetProgress(int mOffsetLow2, int mOffsetHigh2) {
        // TODO Auto-generated method stub
        preOil = (int) (formatFloat((float) (mOffsetLow2 - mOffsetLowInit)
                / total) * 100);
        aftOil = (int) (formatFloat((float) (mOffsetHigh2 - mOffsetLowInit)
                / total) * 100);
        invalidate();
    }

    private float formatFloat(float f) {
        // TODO Auto-generated method stub
        BigDecimal bd = new BigDecimal(f);
        BigDecimal bd1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        f = bd1.floatValue();
        return f;
    }

    public int getAreaFlag(MotionEvent e) {

        int thumb1Top = 0;
        int thumb1Bottom = mThumbHeight / 2 + 15;

        int thumb2Top = mThumbHeight / 2 - 15;
        int thumb2Bottom = mThumbHeight;

        if (e.getY() >= 0 && e.getY() <= mThumbHeight
                && e.getX() >= mOffsetLow + mThumbWidth / 2
                && e.getX() < mOffsetLow + mMiddleLength - mThumbWidth / 2) {

            return CLICK_MIDDLE;
        } else if (e.getY() >= thumb1Top && e.getY() <= thumb1Bottom
                && e.getX() >= mOffsetLow - mThumbWidth / 2
                && e.getX() <= mOffsetLow + mThumbWidth / 2) {
            return CLICK_ON_LOW;
        } else if (e.getY() >= thumb2Top && e.getY() <= thumb2Bottom
                && e.getX() >= mOffsetLow - mThumbWidth / 2 + mMiddleLength
                && e.getX() <= mOffsetLow + mThumbWidth / 2 + mMiddleLength) {
            return CLICK_ON_HIGH;
        } else if (e.getY() >= 0 - 2 * mThumbWidth && e.getY() <= mThumbHeight + 2 * mThumbWidth
                && ((e.getX() >= 0 - 2 * mThumbWidth && e.getX() < mOffsetLow))) {
            return CLICK_IN_LOW_AREA;
        } else if (e.getY() >= 0 - 2 * mThumbWidth
                && e.getY() <= mThumbHeight + 2 * mThumbWidth
                && ((e.getX() > mOffsetHigh + mThumbWidth && e.getX() <= mScollBarWidth + 2 * mThumbWidth))) {
            return CLICK_IN_HIGH_AREA;
        } else {
            return CLICK_INVAILD;
        }
    }

    private int formatInt(double value) {
        BigDecimal bd = new BigDecimal(value);
        BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bd1.intValue();
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener) {
        this.mBarChangeListener = mListener;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(DoubleSeekView seekBar, int preOil,
                               int afterOil);
    }
}
