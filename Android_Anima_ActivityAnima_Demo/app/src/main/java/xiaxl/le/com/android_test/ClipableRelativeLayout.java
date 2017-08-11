package xiaxl.le.com.android_test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 *
 */
public class ClipableRelativeLayout extends RelativeLayout {
    private static final String TAG = ClipableRelativeLayout.class.getSimpleName();


    private RectF mClipRect;

    private boolean mIsCanvasClipSaved = false;

    private int mCanvasSaveCount = -1;

    public ClipableRelativeLayout(Context context) {
        super(context);
    }

    public ClipableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ClipableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    public void setClipRect(RectF rectf) {
        mClipRect = rectf;
        LogUtils.d(TAG, "---setClipRect---");
        LogUtils.d(TAG, "mClipRect: " + mClipRect);
        invalidate();
    }

    public void resetClipRect() {
        LogUtils.d(TAG, "---resetClipRect---");
        mClipRect = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LogUtils.d(TAG, "---onDraw---");

        if (mClipRect != null) {
            if (mIsCanvasClipSaved == false) {
                mCanvasSaveCount = canvas.save();
                mIsCanvasClipSaved = true;
            }
            LogUtils.d(TAG, "mClipRect: " + mClipRect);
            canvas.clipRect(mClipRect);
        } else {
            if (mIsCanvasClipSaved == true) {
                canvas.restoreToCount(mCanvasSaveCount);
                mIsCanvasClipSaved = false;
                mCanvasSaveCount = -1;
            }
        }
    }
}
