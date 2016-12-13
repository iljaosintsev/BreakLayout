package com.turlir.breaklayout;

import android.graphics.Rect;

public class CenterStrategy extends AlignStrategy {

    public CenterStrategy(int start, int stop, int row) {
        super(start, stop, row);
    }

    @Override
    protected int expand(Rect fill, int w, int h, BreakLayoutParams layoutParams,
                         int indexViewInRow, int rowWidth, int pLeft, int pTop) {
        int length = getStop() - getStart();
        int increase = getFreeSpace() / (length + 1);

        fill.left = getStdLeft() + (increase * (indexViewInRow + 1));
        fill.top = getStdTop();
        fill.right = getStdRight() + w;
        fill.bottom = getStdBottom() + h;

        return 0;
    }
}