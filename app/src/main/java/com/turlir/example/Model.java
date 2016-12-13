package com.turlir.example;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.turlir.breaklayout.BreakLayout;
import com.turlir.breaklayout.Incrementable;

class Model implements Incrementable, Parcelable {

    private static final int
            MIN = 2,
            MAX = 6;

    private static final NameMapper MAPPER = new NameMapper() {
        @Override
        public String map(int mode) {
            switch (mode) {
                case BreakLayout.MODE_LEFT:
                    return "LEFT";
                case BreakLayout.MODE_RIGHT:
                    return "RIGHT";
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

    private final String mode;
    private int mId;
    private int mCount;

    Model(int id) {
        this(MAPPER.map(id), id);
    }

    Model(int id, int count) {
        this(MAPPER.map(id), id, count);
    }

    private Model(@NonNull String mode, int id) {
        this(mode, id, 2);
    }

    private Model(@NonNull String mode, int id, int count) {
        this.mode = mode;
        mId = id;
        if (count < MIN || count > MAX) {
            throw new IllegalArgumentException("count must be greater 2");
        }
        mCount = count;
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

    //</editor-fold>

    //<editor-fold desc="Parcelable">

    private Model(Parcel in) {
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

    public static final Creator<Model> CREATOR = new Creator<Model>() {

        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
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

        Model model = (Model) o;

        return mId == model.mId;
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
