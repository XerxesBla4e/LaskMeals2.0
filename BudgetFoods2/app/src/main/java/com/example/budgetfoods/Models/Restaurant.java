package com.example.budgetfoods.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Restaurant implements Parcelable{
    private String restaurantname;
    private String name;
    private String descriptionuniversity;
    private String RId;
    private float ratings;
    private int totalratings;
    private String Uid;
    private String image;
    private String timestamp;

    // Empty constructor required for Firestore
    public Restaurant() {
    }

    public Restaurant(String restaurantname, String name, String descriptionuniversity, String RId, float ratings, int totalratings, String uid, String image, String timestamp) {
        this.restaurantname = restaurantname;
        this.name = name;
        this.descriptionuniversity = descriptionuniversity;
        this.RId = RId;
        this.ratings = ratings;
        this.totalratings = totalratings;
        this.Uid = uid;
        this.image = image;
        this.timestamp = timestamp;
    }

    protected Restaurant(Parcel in) {
        restaurantname = in.readString();
        name = in.readString();
        descriptionuniversity = in.readString();
        RId = in.readString();
        ratings = in.readFloat();
        totalratings = in.readInt();
        Uid = in.readString();
        image = in.readString();
        timestamp = in.readString();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getRestaurantname() {
        return restaurantname;
    }

    public void setRestaurantname(String restaurantname) {
        this.restaurantname = restaurantname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptionuniversity() {
        return descriptionuniversity;
    }

    public void setDescriptionuniversity(String descriptionuniversity) {
        this.descriptionuniversity = descriptionuniversity;
    }

    public String getRId() {
        return RId;
    }

    public void setRId(String RId) {
        this.RId = RId;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public int getTotalratings() {
        return totalratings;
    }

    public void setTotalratings(int totalratings) {
        this.totalratings = totalratings;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(restaurantname);
        parcel.writeString(name);
        parcel.writeString(descriptionuniversity);
        parcel.writeString(RId);
        parcel.writeFloat(ratings);
        parcel.writeInt(totalratings);
        parcel.writeString(Uid);
        parcel.writeString(image);
        parcel.writeString(timestamp);
    }
}
