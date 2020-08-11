package com.xm.vlcdemo.Data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class FileData implements Parcelable {
    Bitmap thumnail;
    String fname;
    String fpath;
    boolean isdirectory;

    public FileData(Bitmap thumnail, String fname, String fpath, boolean isdirectory) {
        this.thumnail = thumnail;
        this.fname = fname;
        this.fpath = fpath;
        this.isdirectory = isdirectory;
    }

    public Bitmap getThumnail() {
        return thumnail;
    }

    public void setThumnail(Bitmap thumnail) {
        this.thumnail = thumnail;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFpath() {
        return fpath;
    }

    public void setFpath(String fpath) {
        this.fpath = fpath;
    }

    public boolean isIsdirectory() {
        return isdirectory;
    }

    public void setIsdirectory(boolean isdirectory) {
        this.isdirectory = isdirectory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.thumnail, flags);
        dest.writeString(this.fname);
        dest.writeString(this.fpath);
        dest.writeByte(this.isdirectory ? (byte) 1 : (byte) 0);
    }

    protected FileData(Parcel in) {
        this.thumnail = in.readParcelable(Bitmap.class.getClassLoader());
        this.fname = in.readString();
        this.fpath = in.readString();
        this.isdirectory = in.readByte() != 0;
    }

    public static final Creator<FileData> CREATOR = new Creator<FileData>() {
        @Override
        public FileData createFromParcel(Parcel source) {
            return new FileData(source);
        }

        @Override
        public FileData[] newArray(int size) {
            return new FileData[size];
        }
    };
}
