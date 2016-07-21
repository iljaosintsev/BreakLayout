package com.turlir.breaklayout;

import android.graphics.Rect;

public class LeftMode extends SimpleMode {

    public LeftMode(int start, int stop, int row) {
        super(start, stop, row);
    }

    @Override
    protected int expand(Rect fill, int w, int h, BreakLayout.BreakLayoutParams layoutParams,
                         int indexViewInRow, int rowWidth, int pLeft, int pTop) {
        int increase = getFreeSpace() / getLength();
        fill.top = getStdTop();
        fill.bottom = getStdBottom() + h;
        if (indexViewInRow != getLength() - 1) {
            fill.left = getStdLeft() + increase;
            fill.right = getStdRight() + w;
        } else {
            fill.left = getParentWidth() - (layoutParams.leftMargin + pLeft + w);
            fill.right = getParentWidth();
        }
        return (increase * (indexViewInRow + 1));
    }
}
