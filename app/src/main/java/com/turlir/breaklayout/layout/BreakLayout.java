package com.turlir.breaklayout.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.turlir.breaklayout.R;

import java.util.HashMap;
import java.util.Map;

public class BreakLayout extends ViewGroup {

    public static final int MODE_RIGHT = 0;
    public static final int MODE_LEFT = 1;
    public static final int MODE_CENTER = 2;
    public static final int MODE_EDGE = 3;
    public static final int MODE_AS_IS = 4;

    private int mMode;
    private int mMiddleRowSpace;

    private Map<Integer, Mode> mStrategy = new HashMap<>();

    public BreakLayout(Context context) {
        super(context);
    }

    public BreakLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BreakLayout);
        try {
            mMode = a.getInteger(R.styleable.BreakLayout_bl_mode, MODE_RIGHT);
            mMiddleRowSpace = (int) a.getDimension(R.styleable.BreakLayout_bl_vertical_margin, 0);
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

        int childMaxHeight = 0;
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
                if (height > childMaxHeight) {
                    childMaxHeight = height;
                }

                // check if child does not fit into current mRowWidth
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
                    current.setPreviousHeight(childMaxHeight * rows);
                }
            }
        }
        // compute parent height based on rows
        int parentHeight = childMaxHeight * (rows + 1);
        parentHeight += getPaddingTop() + getPaddingBottom();
        parentHeight += getMiddleRowSpace() * rows;

        parentHeight = Math.max(parentHeight, getSuggestedMinimumHeight());
        layoutWidth = Math.max(layoutWidth, getSuggestedMinimumWidth());

        int measuredWidth = resolveSizeAndState(layoutWidth, widthMeasureSpec, childState);
        int measuredHeight = resolveSizeAndState(parentHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private Mode selectMode(int start, int stop, int row) {
        Mode tmp;
        switch (mMode) {
            case MODE_RIGHT:
                tmp = new RightMode(start, stop, row);
                break;
            case MODE_LEFT:
                tmp = new LeftMode(start, stop, row);
                break;
            case MODE_CENTER:
                tmp = new CenterMode(start, stop, row);
                break;
            case MODE_EDGE:
                tmp = new EdgeMode(start, stop, row);
                break;
            case MODE_AS_IS:
                tmp = new AsIsMode(start, stop, row);
                break;
            default:
                tmp = new RightMode(start, stop, row);
                break;
        }
        tmp.setMiddleRowSpace(getMiddleRowSpace());
        return tmp;
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

    public int getMiddleRowSpace() {
        return mMiddleRowSpace;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int value) {
        if (value != mMode) {
            mMode = value;
            requestLayout();
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

        void setPreviousHeight(int previousHeight);

        void setMiddleRowSpace(int middleRowSpace);

        void placement(ViewGroup parent);
    }

    public class BreakLayoutParams extends MarginLayoutParams {
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

}
