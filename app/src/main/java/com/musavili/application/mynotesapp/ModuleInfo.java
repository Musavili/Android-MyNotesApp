package com.musavili.application.mynotesapp;

import android.os.Parcel;
import android.os.Parcelable;

public final class ModuleInfo implements Parcelable{
    private final String mModuleId;
    private final String mTitle;
    private boolean mIsComplete = false;

    public ModuleInfo(String moduleId, String title) {
        this(moduleId, title, false);
    }

    public ModuleInfo(String moduleId, String title, boolean isComplete) {
        mModuleId = moduleId;
        mTitle = title;
        mIsComplete = isComplete;
    }

    protected ModuleInfo(Parcel in) {
        mModuleId = in.readString();
        mTitle = in.readString();
        mIsComplete = in.readByte() != 0;
    }

    //information from this field will be used to create new instances of our type
    public static final Creator<ModuleInfo> CREATOR = new Creator<ModuleInfo>() {
        @Override
        public ModuleInfo createFromParcel(Parcel in) {
            return new ModuleInfo(in);
        }

        @Override
        public ModuleInfo[] newArray(int size) {
            return new ModuleInfo[size];
        }
    };

    public String getModuleId() {
        return mModuleId;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isComplete() {
        return mIsComplete;
    }

    public void setComplete(boolean complete) {
        mIsComplete = complete;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleInfo that = (ModuleInfo) o;

        return mModuleId.equals(that.mModuleId);
    }

    @Override
    public int hashCode() {
        return mModuleId.hashCode();
    }

    @Override//override method for the parcelable interface. describes special behaviours for our parcel
    public int describeContents() {
        return 0;
    }

    @Override//parcelable interface method that writes the member fields to the destination parcel
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mModuleId);
        dest.writeString(mTitle);
        dest.writeByte((byte) (mIsComplete ? 1 : 0));
    }

}
