package com.xm.vlcdemo.Data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class FileData implements Parcelable {
    Bitmap thumnail;
    String fname;

    public FileData(Bitmap thumnail, String fname) {
        this.thumnail = thumnail;
        this.fname = fname;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.thumnail, flags);
        dest.writeString(this.fname);
    }

    protected FileData(Parcel in) {
        this.thumnail = in.readParcelable(Bitmap.class.getClassLoader());
        this.fname = in.readString();
    }

    public static final Parcelable.Creator<FileData> CREATOR = new Parcelable.Creator<FileData>() {
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
