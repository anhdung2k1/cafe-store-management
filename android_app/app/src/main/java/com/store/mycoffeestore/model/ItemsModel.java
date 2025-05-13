package com.store.mycoffeestore.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemsModel implements Parcelable {

    private Long productID = null;
    private String title = "";
    private String description = "";
    private ArrayList<String> picUrl = new ArrayList<>();
    private double price = 0.0;
    private float rating = 0;
    private int numberInCart = 0;
    private String extra = "";

    // Parcelable constructor
    protected ItemsModel(Parcel in) {
        productID = (Long) in.readValue(Long.class.getClassLoader());
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
        dest.writeValue(productID);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeStringList(picUrl);
        dest.writeDouble(price);
        dest.writeFloat(rating);
        dest.writeInt(numberInCart);
        dest.writeString(extra);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemsModel> CREATOR = new Creator<>() {
        @Override
        public ItemsModel createFromParcel(Parcel in) {
            return new ItemsModel(in);
        }

        @Override
        public ItemsModel[] newArray(int size) {
            return new ItemsModel[size];
        }
    };
}