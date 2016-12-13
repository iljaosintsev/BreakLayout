package com.turlir.breaklayout.layout;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Mode implements Incrementable, Parcelable {

    private static final int
            MIN = 2,
            MAX = 6;

    private static final NameMapper MAPPER = new NameMapper() {
        @Override
        public String map(int mode) {
            switch (mode) {
                case BreakLayout.MODE_RIGHT:
                    return "RIGHT";
                case BreakLayout.MODE_LEFT:
                    return "LEFT";
                case BreakLayout.MODE_CENTER:
                    return "CENTER";
                case BreakLayout.MODE_EDGE:
                    return "EDGE";
                case BreakLayout.MODE_AS_IS:
                    return "AS IS";
                default:
                    return "RIGHT";
            }
        }
    };

    public final String mode;
    private int mId;
    private int mInitialCount;
    private int mCount;

    public Mode(int id) {
        this(MAPPER.map(id), id);
    }

    public Mode(int id, int count) {
        this(MAPPER.map(id), id, count);
    }

    public Mode(@NonNull String mode, int id) {
        this(mode, id, 2);
    }

    public Mode(@NonNull String mode, int id, int count) {
        this.mode = mode;
        mId = id;
        if (count < MIN || count > MAX) {
            throw new IllegalArgumentException("count must be greater 2");
        }
        mCount = count;
        mInitialCount = mCount;
    }

    //<editor-fold desc="Incrementable">

    @Override
    public boolean isInc() {
        return mCount < MAX; // 0 1 2 3 4 5
    }

    @Override
    public int inc() {
        if (isInc()) {
            mCount += 1;
        }
        return mCount;
    }

    @Override
    public boolean isDec() {
        return mCount > MIN; // 2 3 4 5 6 7
    }

    @Override
    public int dec() {
        if (isDec()) {
            mCount -= 1;
        }
        return mCount;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public void reset() {
        mCount = mInitialCount;
    }

    //</editor-fold>

    //<editor-fold desc="Parcelable">

    private Mode(Parcel in) {
        this.mode = in.readString();
        mId = in.readInt();
        mCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mode);
        dest.writeInt(mId);
        dest.writeInt(mCount);
    }

    public static final Creator<Mode> CREATOR = new Creator<Mode>() {

        @Override
        public Mode createFromParcel(Parcel in) {
            return new Mode(in);
        }

        @Override
        public Mode[] newArray(int size) {
            return new Mode[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    //</editor-fold>

    public int getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mode mode = (Mode) o;

        return mId == mode.mId;
    }

    @Override
    public int hashCode() {
        return mId;
    }

    @Override
    public String toString() {
        return this.mode;
    }

    private interface NameMapper {
        String map(int mode);
    }

}
