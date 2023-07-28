package com.example.budgetfoods.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Restaurant implements Parcelable{
    private String restaurantname;
    private String description;
    private String university;
    private String RId;
    private float ratings;
    private int totalratings;
    private String timestamp;
    private String Uid;
    private String image;


    // Empty constructor required for Firestore
    public Restaurant() {
    }

    public Restaurant(String restaurantname, String description, String university, String RId, float ratings, int totalratings, String timestamp, String uid, String image) {
        this.restaurantname = restaurantname;
        this.description = description;
        this.university = university;
        this.RId = RId;
        this.ratings = ratings;
        this.totalratings = totalratings;
        this.timestamp = timestamp;
        this.Uid = uid;
        this.image = image;
    }

    protected Restaurant(Parcel in) {
        restaurantname = in.readString();
        description = in.readString();
        university = in.readString();
        RId = in.readString();
        ratings = in.readFloat();
        totalratings = in.readInt();
        timestamp = in.readString();
        Uid = in.readString();
        image = in.readString();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(restaurantname);
        parcel.writeString(description);
        parcel.writeString(university);
        parcel.writeString(RId);
        parcel.writeFloat(ratings);
        parcel.writeInt(totalratings);
        parcel.writeString(timestamp);
        parcel.writeString(Uid);
        parcel.writeString(image);
    }
}
