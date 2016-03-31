package com.mobile.yj.screencast.image;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dell on 2016/3/30.
 */
public class ImageInfo implements Parcelable {
    public int id;
    public String title;
    public String data;
    public long size;

    public ImageInfo() {
        // TODO Auto-generated constructor stub
    }

    public ImageInfo(Parcel source) {
        id = source.readInt();
        title = source.readString();
        data = source.readString();
        size = source.readLong();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(data);
        dest.writeLong(size);
    }

    public final static Parcelable.Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {

        @Override
        public ImageInfo createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ImageInfo[size];
        }
    };
}
