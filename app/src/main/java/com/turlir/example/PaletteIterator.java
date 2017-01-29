package com.turlir.example;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;

import java.util.Iterator;

class PaletteIterator implements Iterator<Integer> {

    private final TypedArray mPalette;
    private final int mPrimaryColor;
    private int mIndex;

    PaletteIterator(Context cnt) {
        Resources res = cnt.getResources();
        mPalette = res.obtainTypedArray(R.array.palette);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPrimaryColor = res.getColor(R.color.colorPrimary, cnt.getTheme());
        } else {
            mPrimaryColor = res.getColor(R.color.colorPrimary);
        }
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
