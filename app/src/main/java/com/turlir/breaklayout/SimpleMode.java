package com.turlir.breaklayout;

import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;


public abstract class SimpleMode implements BreakLayout.Mode {

    private final Rect tempChildRect = new Rect(), rect = new Rect();
    private BreakLayout.BreakLayoutParams mLayoutParams;
    private int mParentWidth, mFreeSpace;
    private int mStart, mStop, mLineIndex;
    private int mRowWidth = 0;
    private int mLeftPadding, mTopPadding;
    private int mVerticalMargin;
    private float mMiddleRowSpace;

    public SimpleMode(int start, int stop, int lineIndex) {
        mStart = start;
        mStop = stop;
        mLineIndex = lineIndex;
    }

    @Override
    public void setPreviousHeight(int previousHeight) {
        mVerticalMargin = previousHeight;
    }

    @Override
    public void placement(ViewGroup parent) {
        final int gravity = Gravity.START;
        mTopPadding = parent.getPaddingTop();
        mLeftPadding = parent.getPaddingLeft();
        mRowWidth = 0;

        for (int i = getStart(); i < getStop(); i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                mLayoutParams = (BreakLayout.BreakLayoutParams) child.getLayoutParams();
                int w = child.getMeasuredWidth();
                int h = child.getMeasuredHeight();

                int z = i - getStart();
                mRowWidth += expand(rect, w, h, mLayoutParams, z, mRowWidth, mLeftPadding, mTopPadding)
                        + rect.width();

                Gravity.apply(gravity, w, h, rect, tempChildRect);
                child.layout(tempChildRect.left, tempChildRect.top, tempChildRect.right, tempChildRect.bottom);
            }
        }
    }

    protected abstract int expand(Rect fill, int w, int h, BreakLayout.BreakLayoutParams lp, int i,
                                  int rowWidth, int pLeft, int pTop);

    public int getTop() {
        return (int) (mLineIndex * mMiddleRowSpace);
    }

    public int getParentWidth() {
        return mParentWidth;
    }

    public void setParentWidth(int width) {
        mParentWidth = width;
    }

    public int getFreeSpace() {
        return mFreeSpace;
    }

    public void setFreeSpace(int space) {
        mFreeSpace = space;
    }

    public int getStart() {
        return mStart;
    }

    public void setStart(int i) {
        mStart = i;
    }

    public int getStop() {
        return mStop;
    }

    public void setStop(int i) {
        mStop = i;
    }

    public int getLength() {
        return getStop() - getStart();
    }

    @Override
    public void setMiddleRowSpace(int middleRowSpace) {
        mMiddleRowSpace = middleRowSpace;
    }

    int getStdLeft() {
        return mLayoutParams.leftMargin + mLeftPadding + mRowWidth;
    }

    int getStdTop() {
        return mLayoutParams.topMargin + getTop() + mTopPadding + mVerticalMargin;
    }

    int getStdRight() {
        return mLayoutParams.rightMargin + rect.left;
    }

    int getStdBottom() {
        return rect.top;
    }

}

