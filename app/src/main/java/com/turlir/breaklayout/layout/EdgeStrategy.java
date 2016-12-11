package com.turlir.breaklayout.layout;

import android.graphics.Rect;

public class EdgeStrategy extends AlignStrategy {

    public EdgeStrategy(int start, int stop, int row) {
        super(start, stop, row);
    }

    @Override
    protected int expand(Rect fill, int w, int h, BreakLayoutParams layoutParams,
                         int indexViewInRow, int rowWidth, int pLeft, int pTop) {
        int length = getLength();
        if (getLength() < 2) {
            length = 3;
            indexViewInRow++;
        }
        int increase = getFreeSpace() / (length - 1);
        fill.top = getStdTop();
        fill.bottom = getStdBottom() + h;
        if (indexViewInRow == 0) {
            fill.left = getStdLeft() - rowWidth;
            fill.right = getStdRight() + w;
        } else if (indexViewInRow == getLength() - 1) {
            fill.left = getParentWidth() - (layoutParams.leftMargin + pLeft + w);
            fill.right = getParentWidth();
        } else {
            fill.left = getStdLeft() + increase;
            fill.right = getStdRight() + w;
        }

        return 0;
    }
}