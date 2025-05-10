package com.store.mycoffeestore.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemsModel implements Parcelable {

    private String title;
    private String description;
    private ArrayList<String> picUrl;
    private double price;
    private float rating;
    private int numberInCart;
    private String extra;

    // Default constructor
    public ItemsModel() {
        this.title = "";
        this.description = "";
        this.picUrl = new ArrayList<>();
        this.price = 0.0;
        this.rating = 0;
        this.numberInCart = 0;
        this.extra = "";
    }

    // Constructor with parameters
    public ItemsModel(String title, String description, ArrayList<String> picUrl, double price, float rating, int numberInCart, String extra) {
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.price = price;
        this.rating = rating;
        this.numberInCart = numberInCart;
        this.extra = extra;
    }

    // Constructor for Parcelable
    protected ItemsModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        picUrl = in.createStringArrayList();
        price = in.readDouble();
        rating = in.readFloat();
        numberInCart = in.readInt();
        extra = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeStringList(picUrl);
        dest.writeDouble(price);
        dest.writeDouble(rating);
        dest.writeInt(numberInCart);
        dest.writeString(extra);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Parcelable Creator
    public static final Creator<ItemsModel> CREATOR = new Creator<ItemsModel>() {
        @Override
        public ItemsModel createFromParcel(Parcel in) {
            return new ItemsModel(in);
        }

        @Override
        public ItemsModel[] newArray(int size) {
            return new ItemsModel[size];
        }
    };

    // Getters and Setters for the fields

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(ArrayList<String> picUrl) {
        this.picUrl = picUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
