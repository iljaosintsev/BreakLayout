package com.turlir.breaklayout.layout;

import android.view.ViewGroup;

public interface Strategy {

    int getParentWidth();

    void setParentWidth(int width);

    int getFreeSpace();

    void setFreeSpace(int space);

    int getStart();

    void setStart(int i);

    int getStop();

    void setStop(int i);

    int getPreviousHeight();

    void setPreviousHeight(int previousHeight);

    int getMiddleRowSpace();

    void setMiddleRowSpace(int middleRowSpace);

    void placement(ViewGroup parent);
}
