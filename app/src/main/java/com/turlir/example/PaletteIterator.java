package com.turlir.example;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Infinite color iterator from palette.xml
 */
class PaletteIterator implements ListIterator<Integer> {

    private final List<Integer> mColors;
    private int mIndex;

    PaletteIterator(Context cnt) {
        Resources res = cnt.getResources();

        int primaryColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            primaryColor = res.getColor(R.color.colorPrimary, cnt.getTheme());
        } else {
            primaryColor = res.getColor(R.color.colorPrimary);
        }

        TypedArray palette = res.obtainTypedArray(R.array.palette);
        int count = palette.length();
        mColors = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int color = palette.getColor(i, primaryColor);
            mColors.add(color);
        }
        palette.recycle();

        mIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return nextIndex() < mColors.size(); // always true
    }

    @Override
    public int nextIndex() {
        int next = mIndex + 1;
        if (next >= mColors.size()) {
            return 0;
        }
        return next;
    }

    @Override
    @ColorInt
    public Integer next() {
        int color = mColors.get(mIndex);
        mIndex = nextIndex();
        return color;
    }

    @Override
    public boolean hasPrevious() {
        return previousIndex() > -1; // always true
    }

    @Override
    public int previousIndex() {
        int prev = mIndex - 1;
        if (prev < 0) {
            return mColors.size() - 1;
        }
        return prev;
    }

    @Override
    public Integer previous() {
        int color = mColors.get(mIndex);
        mIndex = previousIndex();
        return color;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Integer value) {
        mColors.set(mIndex, value);
    }

    @Override
    public void add(Integer value) {
        mColors.add(value);
    }
}
