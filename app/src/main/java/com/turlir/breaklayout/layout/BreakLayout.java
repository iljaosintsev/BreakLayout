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

    public static final int MODE_LEFT = 0;
    public static final int MODE_RIGHT = 1;
    public static final int MODE_CENTER = 2;
    public static final int MODE_EDGE = 3;
    public static final int MODE_AS_IS = 4;

    private int mMode;
    private int mMiddleRowSpace;

    private Map<Integer, Strategy> mStrategy = new HashMap<>();

    public BreakLayout(Context context) {
        super(context);
    }

    public BreakLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BreakLayout);
        try {
            mMode = a.getInteger(R.styleable.BreakLayout_bl_mode, MODE_LEFT);
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
        int firstChildInRow = 0;

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                childState = combineMeasuredStates(childState, child.getMeasuredState());

                BreakLayoutParams lp = (BreakLayoutParams) child.getLayoutParams();
                final int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                final int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

                // determine max childHeight
                if (childHeight > childMaxHeight) {
                    childMaxHeight = childHeight;
                }

                // check if child does not fit into current mRowWidth
                if (rowWidth + childWidth > layoutWidth && i != 0) {
                    rowWidth = 0;
                    rows++;
                    firstChildInRow = i;
                }

                rowWidth += childWidth;

                Strategy current = mStrategy.get(rows);
                if (current == null) {
                    current = selectMode(firstChildInRow, i + 1, rows);
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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < mStrategy.size(); i++) {
            Strategy strategy = mStrategy.get(i);
            strategy.placement(this);
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

    public void setMiddleRowSpace(int value) {
        mMiddleRowSpace = value;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int value) {
        mMode = value;
    }

    private Strategy selectMode(int start, int stop, int row) {
        Strategy tmp;
        switch (mMode) {
            case MODE_LEFT:
                tmp = new LeftStrategy(start, stop, row);
                break;
            case MODE_RIGHT:
                tmp = new RighStrategy(start, stop, row);
                break;
            case MODE_CENTER:
                tmp = new CenterStrategy(start, stop, row);
                break;
            case MODE_EDGE:
                tmp = new EdgeStrategy(start, stop, row);
                break;
            case MODE_AS_IS:
                tmp = new AsIsStrategy(start, stop, row);
                break;
            default:
                tmp = new LeftStrategy(start, stop, row);
                break;
        }
        tmp.setMiddleRowSpace(getMiddleRowSpace());
        return tmp;
    }

}
