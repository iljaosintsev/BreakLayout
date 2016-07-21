package com.turlir.breaklayout.layout;

import android.graphics.Rect;

public class AsIsMode extends SimpleMode {

    public AsIsMode(int start, int stop, int row) {
        super(start, stop, row);
    }

    @Override
    protected int expand(Rect fill, int w, int h, BreakLayout.BreakLayoutParams layoutParams,
                         int indexViewInRow, int rowWidth, int pLeft, int pTop) {
        fill.left = getStdLeft();
        fill.top = getStdTop();
        fill.right = getStdRight() + w;
        fill.bottom = getStdBottom() + h;
        return 0;
    }
}