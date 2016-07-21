package com.turlir.breaklayout.layout;

import android.graphics.Rect;

public class RightMode extends SimpleMode {

    public RightMode(int start, int stop, int row) {
        super(start, stop, row);
    }

    @Override
    protected int expand(Rect fill, int w, int h, BreakLayout.BreakLayoutParams layoutParams,
                         int indexViewInRow, int rowWidth, int pLeft, int pTop) {
        int increase = getFreeSpace() / (getLength());

        if (indexViewInRow > 0) {
            fill.left = increase;
        } else {
            fill.left = 0;
        }

        fill.left += getStdLeft();
        fill.top = getStdTop();
        fill.right = getStdRight() + w;
        fill.bottom = getStdBottom() + h;

        return (increase * indexViewInRow);
    }

}
