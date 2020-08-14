package com.xm.vlcdemo.Data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageData implements Parcelable {
    Bitmap thumbnail;
    String path;
    String fname;

    public ImageData(Bitmap thumbnail, String path, String fname) {
        this.thumbnail = thumbnail;
        this.path = path;
        this.fname = fname;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeString(this.path);
        dest.writeString(this.fname);
    }

    protected ImageData(Parcel in) {
        this.thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
        this.path = in.readString();
        this.fname = in.readString();
    }

    public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel source) {
            return new ImageData(source);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };
}

