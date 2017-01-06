package com.turlir.example;


import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;

import java.util.Iterator;

class PaletteIterator implements Iterator<Integer> {

    private final TypedArray mPalette;
    private final int mPrimaryColor;
    private int mIndex;

    PaletteIterator(Resources res) {
        mPalette = res.obtainTypedArray(R.array.palette);
        mPrimaryColor = res.getColor(R.color.colorPrimary);
        mIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    @ColorInt
    public Integer next() {
        int color = mPalette.getColor(mIndex, mPrimaryColor);
        mIndex = mIndex + 1;
        if (mIndex >= mPalette.length()) {
            mIndex = 0;
        }
        return color;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
