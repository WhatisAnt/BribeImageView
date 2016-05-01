package io.github.leibnik.bribeimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Droidroid on 2016/5/1.
 */
public class BribeImageView extends ImageView {

    private final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private final int DEFAULT_COLOR_DRAWABLE_DIMENSION = dp2px(250);
    private static final int BLUR_IMAGE = 0;
    private static final int TO_DRAW_CIRCLE = 1;
    private Bitmap mBackgroundBitmap;
    private Bitmap mBlurryBitmap;
    private Bitmap mTargetBitmap;
    private Canvas mCanvas;
    private Paint mCirclePaint;
    private int mCircleCenterX;
    private int mCircleCenterY;
    private int mCircleRadius;
    private int mPeriod;
    private int mBlurRadius;
    private float mScaleFactor;
    private Handler mHandler;
    private boolean toDrawCircle;
    private boolean toBlur = true;
    private OnBlurCompletedListener mListener;
    private boolean toPerformListener = true;

    private void initHandler() {
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case BLUR_IMAGE:
                        mBlurryBitmap = getScaledBitmap(mBlurryBitmap, mScaleFactor);
                        mCircleCenterX = (int) (mCircleRadius + Math.random() * (mBlurryBitmap.getWidth() - 2 * mCircleRadius));
                        mCircleCenterY = (int) (mCircleRadius + Math.random() * (mBlurryBitmap.getHeight() - 2 * mCircleRadius));
                        mTargetBitmap = Bitmap.createBitmap(mBlurryBitmap.getWidth(), mBlurryBitmap.getHeight()
                                , Bitmap.Config.ARGB_8888);
                        mCanvas = new Canvas(mTargetBitmap);
                        toBlur = true;
                        invalidate();
                        mHandler.sendEmptyMessage(TO_DRAW_CIRCLE);
                        break;
                    case TO_DRAW_CIRCLE:
                        toDrawCircle = !toDrawCircle;
                        invalidate();
                        mHandler.sendEmptyMessageDelayed(TO_DRAW_CIRCLE, mPeriod);
                        break;

                }
            }
        };
    }

    private Bitmap getScaledBitmap(Bitmap bitmap, float mScaleFactor) {
        Bitmap overlay = Bitmap.createBitmap((int) (bitmap.getWidth() * mScaleFactor),
                (int) (bitmap.getHeight() * mScaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(mScaleFactor, mScaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return overlay;
    }

    public interface OnBlurCompletedListener {
        void blurCompleted();
    }

    public void setOnBlurCompletedListener(OnBlurCompletedListener listener) {
        this.mListener = listener;
    }

    public BribeImageView(Context context) {
        this(context, null);
    }

    public BribeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BribeImageView);
        mCircleRadius = ta.getDimensionPixelSize(R.styleable.BribeImageView_circle_radius, dp2px(30));
        mBlurRadius = ta.getInt(R.styleable.BribeImageView_blur_radius, 6);
        mPeriod = ta.getInt(R.styleable.BribeImageView_period, 1000);
        mScaleFactor = ta.getFloat(R.styleable.BribeImageView_scale_factor, 8.0f);
        ta.recycle();
        initHandler();
        mCirclePaint = new Paint();
        mCirclePaint.setAlpha(0);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBackgroundBitmap == null || mBlurryBitmap == null) {
            return;
        }
        if (toBlur) {
            if (mListener != null && toPerformListener) {
                mListener.blurCompleted();
                toPerformListener = false;
            }
            if (toDrawCircle) {
                mCanvas.drawBitmap(mBlurryBitmap, 0, 0, null);
                mCanvas.drawCircle(mCircleCenterX, mCircleCenterY, mCircleRadius, mCirclePaint);
            } else {
                mCanvas.drawBitmap(mBlurryBitmap, 0, 0, null);
            }
            canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
            canvas.drawBitmap(mTargetBitmap, 0, 0, null);
        } else {
            canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (getDrawable() == null) {
            return;
        }
        mBackgroundBitmap = getBitmapFromDrawable(getDrawable());
        blurImage();
    }


    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (getDrawable() == null) {
            return;
        }
        mBackgroundBitmap = getBitmapFromDrawable(getDrawable());
        blurImage();

    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (getDrawable() == null) {
            return;
        }
        mBackgroundBitmap = getBitmapFromDrawable(getDrawable());
        blurImage();

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (bm == null) {
            return;
        }
        mBackgroundBitmap = bm;
        blurImage();

    }

    @Override
    public void setImageIcon(Icon icon) {
        super.setImageIcon(icon);
        if (getDrawable() == null) {
            return;
        }
        mBackgroundBitmap = getBitmapFromDrawable(getDrawable());
        blurImage();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        blurImage();
    }

    private void blurImage() {
        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }
        if (mBackgroundBitmap == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap overlay = getScaledBitmap(mBackgroundBitmap, 1 / mScaleFactor);
                mBlurryBitmap = FastBlur.doBlur(overlay, mBlurRadius, true);
                mHandler.sendEmptyMessage(BLUR_IMAGE);
            }
        }).start();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap;
        if (drawable instanceof ColorDrawable) {
            bitmap = Bitmap.createBitmap(DEFAULT_COLOR_DRAWABLE_DIMENSION
                    , DEFAULT_COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth()
                    , drawable.getIntrinsicHeight(), BITMAP_CONFIG);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private int dp2px(int dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setToBlur(boolean toBlur) {
        this.toBlur = toBlur;
        invalidate();
    }
}
