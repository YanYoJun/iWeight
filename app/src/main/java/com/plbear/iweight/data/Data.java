package com.plbear.iweight.data;

/**
 * Created by yanyongjun on 2018/6/11.
 */

public class Data {
    private int mId;
    private long mTime;
    private float mWeight;
    private boolean mIsEditMode = false;

    public boolean temp = false; //临时字段，不做任何的用途

    public Data(int id,long time,float weight){
        mId = id;
        mTime = time;
        mWeight = weight;
    }


    public Data(){

    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public long getTime() {
        return mTime;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setEditMode(boolean mode){
        mIsEditMode = mode;
    }

    public boolean isEditMode(){
        return mIsEditMode;
    }

    public Data clone(){
        Data data = new Data(this.mId,this.mTime,this.mWeight);
        return data;
    }

}

