package com.turlir.breaklayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class BreakLayoutParams extends ViewGroup.MarginLayoutParams {

    public BreakLayoutParams() {
        this(BreakLayoutParams.WRAP_CONTENT, BreakLayoutParams.WRAP_CONTENT);
    }

    public BreakLayoutParams(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    public BreakLayoutParams(int width, int height) {
        super(width, height);
    }

    public BreakLayoutParams(ViewGroup.LayoutParams source) {
        super(source);
    }

}
