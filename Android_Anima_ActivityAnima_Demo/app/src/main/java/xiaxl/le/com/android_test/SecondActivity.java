package xiaxl.le.com.android_test;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

public class SecondActivity extends Activity {
    private static final String TAG = SecondActivity.class.getSimpleName();



    public static final String INTENT_KEY_WINDOW_ANIMATION_SATRT_Y1 = "intent_key_windowanimationstarty1";

    public static final String INTENT_KEY_WINDOW_ANIMATION_SATRT_Y2 = "intent_key_windowanimationstarty2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.second_main);
        //
        View mLayout = findViewById(R.id.second_root_layout);
        //
        int windowAnimationStartY1 = getIntent().getIntExtra(INTENT_KEY_WINDOW_ANIMATION_SATRT_Y1, 500);
        int windowAnimationStartY2 = getIntent().getIntExtra(INTENT_KEY_WINDOW_ANIMATION_SATRT_Y2, 500);
        //
        LogUtils.d(TAG,"windowAnimationStartY1: "+windowAnimationStartY1);
        LogUtils.d(TAG,"windowAnimationStartY2: "+windowAnimationStartY2);
        //
        if (windowAnimationStartY1 != 0 && windowAnimationStartY2 != 0) {
            setupNewsTabItemOpenActivityAnimation(this, mLayout, windowAnimationStartY1, windowAnimationStartY2);
        }

    }


    public static void setupNewsTabItemOpenActivityAnimation(Activity activity, View rootView, final int animationStartY1, final int animationStartY2) {
        if (rootView == null || ! (rootView instanceof ClipableRelativeLayout)) {
            return;
        }

        activity.overridePendingTransition(0, R.anim.exit_alpha);
        final View attachedRootView = rootView;
        attachedRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Context context = attachedRootView.getContext();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    attachedRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    attachedRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int animationY1 = animationStartY1;
                int animationY2 = animationStartY2;
                int rootWidth = attachedRootView.getWidth();
                int rootHeight = attachedRootView.getHeight();
                if (rootWidth == 0 || rootHeight == 0) {
                    DisplayMetrics dm = context.getResources().getDisplayMetrics();
                    rootWidth = dm.widthPixels;
                    rootHeight = dm.heightPixels;
                }
                if (animationY1 <= 0 || animationY2 <= 0) {
                    animationY1 = rootHeight/2;
                    animationY2 = rootHeight/2;
                } else {
                    // 因为animationY1和Y2是通过getLocationOnScreen取得，可能包含statusbar的高度
                    // 所以这里要使rootView对准animationY1和Y2，必须要减去相应的偏移量
                    int[] rootViewLoation = new int[2];
                    attachedRootView.getLocationOnScreen(rootViewLoation);
                    animationY1 -= rootViewLoation[1];
                    animationY2 -= rootViewLoation[1];
                }

                RectF rectStart = new RectF(0, animationY1, rootWidth, animationY2);
                RectF rectEnd = new RectF(0, 0, rootWidth, rootHeight);

                ((ClipableRelativeLayout)attachedRootView).setClipRect(rectStart);
                ClipAnimator clipAnimation = ClipAnimator.ofRect(rectStart, rectEnd);

                clipAnimation.setDuration(300);

                clipAnimation.setInterpolator(new AccelerateInterpolator());
                clipAnimation.setTarget(attachedRootView);
                clipAnimation.addUpdateListener(new ClipAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(Object target, RectF rectF) {
                        if (target instanceof ClipableRelativeLayout) {
                            ((ClipableRelativeLayout)target).setClipRect(rectF);
                        }
                    }

                    @Override
                    public void onAnimationFinished(Object target) {
                        if (target instanceof ClipableRelativeLayout) {
                            ((ClipableRelativeLayout)target).resetClipRect();
                        }
                    }
                });
                clipAnimation.start();
            }
        });

    }


}

