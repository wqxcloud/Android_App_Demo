package xiaxl.le.com.android_test;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.RectF;

/**
 * 
 */
public class ClipAnimator {

    /**
     * @hide
     */
    protected float mFromLeftValue = 0.0f;
    /**
     * @hide
     */
    protected float mToLeftValue = 0.0f;

    /**
     * @hide
     */
    protected float mFromTopValue = 0.0f;
    /**
     * @hide
     */
    protected float mToTopValue = 0.0f;

    /**
     * @hide
     */
    protected float mFromRightValue = 0.0f;
    /**
     * @hide
     */
    protected float mToRightValue = 0.0f;

    /**
     * @hide
     */
    protected float mFromBottomValue = 0.0f;
    /**
     * @hide
     */
    protected float mToBottomValue = 0.0f;

    private ValueAnimator mValueAnimator;

    private Object mTarget;

    private AnimatorUpdateListener mAnimatorUpdateListener;

    public interface AnimatorUpdateListener {
        public void onAnimationUpdate(Object target, RectF rectF);
        public void onAnimationFinished(Object target);
    }

    private ClipAnimator(RectF fromRect, RectF toRect) {
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
        mFromLeftValue = fromRect.left;
        mToLeftValue = toRect.left;
        mFromTopValue = fromRect.top;
        mToTopValue = toRect.top;

        mFromRightValue = fromRect.right;
        mToRightValue = toRect.right;
        mFromBottomValue = fromRect.bottom;
        mToBottomValue = toRect.bottom;
    }

    public static ClipAnimator ofRect(RectF fromRect, RectF toRect) {
        ClipAnimator animator = new ClipAnimator(fromRect, toRect);
        return animator;
    }

    public void setTarget(Object target) {
        mTarget = target;
    }

    public void setClipRect(RectF fromRect, RectF toRect) {
        mFromLeftValue = fromRect.left;
        mToLeftValue = toRect.left;
        mFromTopValue = fromRect.top;
        mToTopValue = toRect.top;

        mFromRightValue = fromRect.right;
        mToRightValue = toRect.right;
        mFromBottomValue = fromRect.bottom;
        mToBottomValue = toRect.bottom;
    }

    public void setDuration(long duration) {
        mValueAnimator.setDuration(duration);
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        mValueAnimator.setInterpolator(interpolator);
    }

    public void start() {
        mValueAnimator.start();
    }

    public void cancel() {
        mValueAnimator.cancel();
    }

    public void end() {
        mValueAnimator.end();
    }

    public void addUpdateListener(AnimatorUpdateListener listener) {
        mAnimatorUpdateListener = listener;
        mValueAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float interpolatedTime = (float)animation.getAnimatedValue();
                        float left = mToLeftValue, right = mToRightValue, top = mToTopValue, bottom = mToBottomValue;
                        if (mFromLeftValue != mToLeftValue) {
                            left = mFromLeftValue + ((mToLeftValue - mFromLeftValue) * interpolatedTime);
                        }
                        if (mFromRightValue != mToRightValue) {
                            right = mFromRightValue + ((mToRightValue - mFromRightValue) * interpolatedTime);
                        }
                        if (mFromTopValue != mToTopValue) {
                            top = mFromTopValue + ((mToTopValue - mFromTopValue) * interpolatedTime);
                        }
                        if (mFromBottomValue != mToBottomValue) {
                            bottom = mFromBottomValue + ((mToBottomValue - mFromBottomValue) * interpolatedTime);
                        }
                        mAnimatorUpdateListener.onAnimationUpdate(mTarget, new RectF(left, top, right, bottom));
                    }
                }
        );
        mValueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorUpdateListener.onAnimationFinished(mTarget);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
