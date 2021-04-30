package com.abbvisor.abbvisor.util.animation;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by natavit on 1/17/2017 AD.
 */

public class ResizeAnimation extends Animation {

    private int mMarginStart;
    private int mMarginEnd;

    private CardView mView;
    private BottomSheetBehavior mBottomSheetBehavior;

    public ResizeAnimation(CardView view, BottomSheetBehavior bottomSheetBehavior, int margin) {
        mView = view;
        mBottomSheetBehavior = bottomSheetBehavior;
        mMarginStart = mMarginEnd = margin;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        CoordinatorLayout.LayoutParams params
                = (CoordinatorLayout.LayoutParams) mView.getLayoutParams();
        params.setMarginStart((int)(mMarginStart * interpolatedTime));
        params.setMarginEnd((int)(mMarginEnd * interpolatedTime));
        params.setBehavior(mBottomSheetBehavior);
        mView.setLayoutParams(params);
    }

}
