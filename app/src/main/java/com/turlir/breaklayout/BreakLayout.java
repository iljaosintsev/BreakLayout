package com.turlir.breaklayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BreakLayout extends ViewGroup {

    public static final int MODE_RIGHT = 0;
    public static final int MODE_LEFT = 1;
    public static final int MODE_CENTER = 2;
    public static final int MODE_EDGE = 3;
    public static final int MODE_AS_IS = 4;

    /**
     * Maximum height of the child views.
     */
    private int mChildMaxHeight = 0;

    private int mMode;

    private Map<Integer, Mode> mStrategy = new HashMap<>();;

    public BreakLayout(Context context) {
        super(context);
    }

    public BreakLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BreakLayout);
        try {
            mMode = a.getInteger(R.styleable.BreakLayout_bl_mode, MODE_RIGHT);
        } finally {
            a.recycle();
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mStrategy.clear();

        mChildMaxHeight = 0;
        int layoutWidth = getMeasuredWidth();
        int childState = 0;
        int rowWidth = 0;
        int rows = 0;
        int k = 0;

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                childState = combineMeasuredStates(childState, child.getMeasuredState());

                BreakLayoutParams lp = (BreakLayoutParams) child.getLayoutParams();
                final int height = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                final int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

                // determine max height
                if (height > mChildMaxHeight) {
                    mChildMaxHeight = height;
                }

                // check if child does not fit into current rowWidth
                if (rowWidth + childWidth > layoutWidth && i != 0) {
                    rowWidth = 0;
                    rows++;
                    k = i;
                }

                rowWidth += childWidth;

                Mode current = mStrategy.get(rows);
                if (current == null) {
                    current = selectMode(k, i + 1, rows);
                    current.setParentWidth(layoutWidth);
                    mStrategy.put(rows, current);
                } else {
                    current.setStop(i + 1);
                }
                current.setFreeSpace(layoutWidth - rowWidth);
                if (rows > 0) {
                    current.setRowMargin(mChildMaxHeight);
                }
            }
        }

        // compute parent height based on rows
        int parentHeight = mChildMaxHeight * (rows + 1);
        parentHeight += getPaddingTop() + getPaddingBottom();
        parentHeight += 50 * rows;

        parentHeight = Math.max(parentHeight, getSuggestedMinimumHeight());
        layoutWidth = Math.max(layoutWidth, getSuggestedMinimumWidth());

        Log.d("turlir", "onMeasure parentHeight = " + parentHeight + " layoutWidth " + layoutWidth);

        int measuredWidth = resolveSizeAndState(layoutWidth, widthMeasureSpec, childState);
        int measuredHeight = resolveSizeAndState(parentHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private Mode selectMode(int start, int stop, int row) {
        switch (mMode) {
            case MODE_RIGHT:
                return new RightMode(start, stop, row);
            case MODE_LEFT:
                return new LeftMode(start, stop, row);
            case MODE_CENTER:
                return new CenterMode(start, stop, row);
            case MODE_EDGE:
                return new EdgeMode(start, stop, row);
            case MODE_AS_IS:
                return new AsIsMode(start, stop, row);
            default:
                return new RightMode(start, stop, row);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < mStrategy.size(); i++) {
            Mode mode = mStrategy.get(i);
            mode.placement(this);
        }
    }

    @Override
    public BreakLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new BreakLayoutParams(getContext(), attrs);
    }

    @Override
    protected BreakLayoutParams generateDefaultLayoutParams() {
        return new BreakLayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new BreakLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams lp) {
        return lp instanceof BreakLayoutParams;
    }

    public static class BreakLayoutParams extends MarginLayoutParams {
        public BreakLayoutParams() {
            this(BreakLayoutParams.WRAP_CONTENT, BreakLayoutParams.WRAP_CONTENT);
        }

        public BreakLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public BreakLayoutParams(int width, int height) {
            super(width, height);
        }

        public BreakLayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public interface Mode {

        int getParentWidth();

        void setParentWidth(int width);

        int getFreeSpace();

        void setFreeSpace(int space);

        int getStart();

        void setStart(int i);

        int getStop();

        void setStop(int i);

        void placement(ViewGroup parent);

        void setRowMargin(int rowMargin);
    }

    public static abstract class SimpleMode implements Mode {

        private final String TAG = getClass().getSimpleName();

        private final Rect tempChildRect = new Rect(), rect = new Rect();
        private BreakLayoutParams mLp;
        private int parentWidth, freeSpace;
        private int start, stop, index;
        protected int rowWidth = 0;
        private int mLeftPadding, mTopPadding;
        private int mVerticalMargin;

        public SimpleMode(int start, int stop, int index) {
            this.start = start;
            this.stop = stop;
            this.index = index;
        }

        @Override
        public void setRowMargin(int rowMargin) {
            mVerticalMargin = rowMargin;
        }

        @Override
        public void placement(ViewGroup parent) {
            final int gravity = Gravity.START;
            mTopPadding = parent.getPaddingTop();
            mLeftPadding = parent.getPaddingLeft();
            rowWidth = 0;

            for (int i = getStart(); i < getStop(); i++) {
                View child = parent.getChildAt(i);
                if (child.getVisibility() != View.GONE) {
                    mLp = (BreakLayoutParams) child.getLayoutParams();
                    int w = child.getMeasuredWidth();
                    int h = child.getMeasuredHeight();

                    int z = i - getStart();
                    rowWidth += expand(rect, w, h, mLp, z, rowWidth, mLeftPadding, mTopPadding)
                            + rect.width();

                    String msg = String.format(Locale.getDefault(),"placement: %d %d %d %d %d",
                            index, rect.left, rect.top, rect.right, rect.bottom);
                    Log.d(TAG, msg);

                    Gravity.apply(gravity, w, h, rect, tempChildRect);
                    child.layout(tempChildRect.left, tempChildRect.top, tempChildRect.right, tempChildRect.bottom);
                }
            }
        }

        protected abstract int expand(Rect fill, int w, int h, BreakLayoutParams lp, int i,
                                      int rowWidth, int pLeft, int pTop);

        public int getTop() {
            return index * 50;
        }

        @Override
        public int getParentWidth() {
            return parentWidth;
        }

        @Override
        public void setParentWidth(int width) {
            parentWidth = width;
        }

        @Override
        public int getFreeSpace() {
            return freeSpace;
        }

        @Override
        public void setFreeSpace(int space) {
            freeSpace = space;
        }

        @Override
        public int getStart() {
            return start;
        }

        @Override
        public int getStop() {
            return stop;
        }

        @Override
        public void setStop(int i) {
            stop = i;
        }

        @Override
        public void setStart(int i) {
            start = i;
        }

        public int getLength() {
            return getStop() - getStart();
        }

        int getStdLeft() {
            return mLp.leftMargin + mLeftPadding + rowWidth;
        }

        int getStdTop() {
            return mLp.topMargin + getTop() + mTopPadding + mVerticalMargin;
        }

        int getStdRight() {
            return mLp.rightMargin + rect.left;
        }

        int getStdBottom() {
            return rect.top;
        }

    }

    public static class RightMode extends SimpleMode {

        public RightMode(int start, int stop, int row) {
            super(start, stop, row);
        }

        @Override
        protected int expand(Rect fill, int w, int h, BreakLayoutParams lp, int i, int rowWidth, int pLeft, int pTop) {
            int increase = getFreeSpace() / (getLength());

            if (i > 0) {
                fill.left = increase;
            } else {
                fill.left = 0;
            }

            fill.left += getStdLeft();
            fill.top = getStdTop();
            fill.right = getStdRight() + w;
            fill.bottom = getStdBottom() + h;

            return (increase * i);
        }

    }

    public static class LeftMode extends SimpleMode {

        public LeftMode(int start, int stop, int row) {
            super(start, stop, row);
        }

        @Override
        protected int expand(Rect fill, int w, int h, BreakLayoutParams lp, int i, int rowWidth, int pLeft, int pTop) {
            int increase = getFreeSpace() / getLength();
            fill.top = getStdTop();
            fill.bottom = getStdBottom() + h;
            if (i != getLength() - 1) {
                fill.left = getStdLeft() + increase;
                fill.right = getStdRight() + w;
            } else {
                fill.left = getParentWidth() - (lp.leftMargin + pLeft + w);
                fill.right = getParentWidth();
            }
            return (increase * (i + 1));
        }
    }

    public static class CenterMode extends SimpleMode {

        public CenterMode(int start, int stop, int row) {
            super(start, stop, row);
        }

        @Override
        protected int expand(Rect fill, int w, int h, BreakLayoutParams lp, int i, int rowWidth, int pLeft, int pTop) {
            int length = getStop() - getStart();
            int increase = getFreeSpace() / (length + 1);

            fill.left = getStdLeft() + (increase * (i + 1));
            fill.top = getStdTop();
            fill.right = getStdRight() + w;
            fill.bottom = getStdBottom() + h;

            return 0;
        }
    }

    public static class EdgeMode extends SimpleMode {

        public EdgeMode(int start, int stop, int row) {
            super(start, stop, row);
        }

        @Override
        protected int expand(Rect fill, int w, int h, BreakLayoutParams lp, int i, int rowWidth, int pLeft, int pTop) {
            int length = getLength();
            if (getLength() < 1) {
                length = 3;
                i++;
            }
            int increase = getFreeSpace() / (length - 1);
            fill.top = getStdTop();
            fill.bottom = getStdBottom() + h;
            if (i == 0) {
                fill.left = getStdLeft() - rowWidth;
                fill.right = getStdRight() + w;
            } else if (i == getLength() - 1) {
                fill.left = getParentWidth() - (lp.leftMargin + pLeft + w);
                fill.right = getParentWidth();
            } else {
                fill.left = getStdLeft() + increase;
                fill.right = getStdRight() + w;
            }

            return 0;
        }
    }

    public static class AsIsMode extends SimpleMode {

        public AsIsMode(int start, int stop, int row) {
            super(start, stop, row);
        }

        @Override
        protected int expand(Rect fill, int w, int h, BreakLayoutParams lp, int i, int rowWidth, int pLeft, int pTop) {
            fill.left = getStdLeft();
            fill.top = getStdTop();
            fill.right = getStdRight() + w;
            fill.bottom = getStdBottom() + h;
            return 0;
        }
    }

}
