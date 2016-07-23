package com.turlir.breaklayout.layout;

import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

public abstract class AlignStrategy implements BreakLayout.Strategy {

    private BreakLayout.BreakLayoutParams mLayoutParams;
    private final Rect mTempChildRect, mChildRect;
    private int mParentWidth, mFreeSpace;
    private int mStart, mStop, mLineIndex;
    private int mVerticalMargin, mMiddleRowSpace;
    private int mRowWidth;
    private int mLeftPadding, mTopPadding;

    public AlignStrategy(int start, int stop, int lineIndex) {
        mStart = start;
        mStop = stop;
        mLineIndex = lineIndex;
        mTempChildRect = new Rect();
        mChildRect = new Rect();
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
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();

                int index = i - getStart();
                int delta = expand(mChildRect, width, height, mLayoutParams, index,
                        mRowWidth, mLeftPadding, mTopPadding);
                mRowWidth += delta + mChildRect.width();

                Gravity.apply(gravity, width, height, mChildRect, mTempChildRect);
                child.layout(mTempChildRect.left, mTempChildRect.top, mTempChildRect.right,
                        mTempChildRect.bottom);
            }
        }
    }

    protected abstract int expand(Rect fill, int w, int h, BreakLayout.BreakLayoutParams lp, int i,
                                  int rowWidth, int pLeft, int pTop);

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

    @Override
    public int getMiddleRowSpace() {
        return mMiddleRowSpace;
    }

    @Override
    public void setMiddleRowSpace(int middleRowSpace) {
        mMiddleRowSpace = middleRowSpace;
    }

    @Override
    public int getPreviousHeight() {
        return mVerticalMargin;
    }

    @Override
    public void setPreviousHeight(int previousHeight) {
        mVerticalMargin = previousHeight;
    }

    protected int getTop() {
        return mLineIndex * mMiddleRowSpace;
    }

    protected int getLength() {
        return getStop() - getStart();
    }

    protected int getStdLeft() {
        return mLayoutParams.leftMargin + mLeftPadding + mRowWidth;
    }

    protected int getStdTop() {
        return mLayoutParams.topMargin + getTop() + mTopPadding + mVerticalMargin;
    }

    protected int getStdRight() {
        return mLayoutParams.rightMargin + mChildRect.left;
    }

    protected int getStdBottom() {
        return mChildRect.top;
    }

}

