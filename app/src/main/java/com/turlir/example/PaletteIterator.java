package com.turlir.example;


import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.util.Log;

import java.util.Iterator;

class PaletteIterator implements Iterator<Integer> {

    private TypedArray mPalette;
    private int mPrimaryColor;
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
        Log.d("PaletteIterator", String.valueOf(mIndex));
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
