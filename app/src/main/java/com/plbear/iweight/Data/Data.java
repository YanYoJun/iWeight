package com.plbear.iweight.Data;

/**
 * Created by koakira on 16/11/5.
 */
public class Data {
    private int mId = -1;
    private long mTime;
    private float mWeight;

    public Data(int id, long time, float weight) {
        init(id, time, weight);
    }

    public Data() {

    }

    private void init(int id, long time, float weight) {
        mTime = time;
        mId = id;
        mWeight = weight;
    }

    public String toString() {
        return "Data(" + mId + "," + mTime + "," + mWeight + ")";
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }

    public long getTime() {
        return mTime;
    }

    public float getWeight() {
        return mWeight;
    }

    public boolean equals(Data data) {
        if (data == null) {
            return false;
        }
        return mTime == data.getTime();
    }

    public int getId() {
        return mId;
    }

}