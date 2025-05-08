package com.store.mycoffeestore.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;



import com.google.android.material.imageview.ShapeableImageView;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.SizeAdapter;
import com.store.mycoffeestore.helper.ManagementCart;
import com.store.mycoffeestore.model.ItemsModel;

import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    private ItemsModel item;
    private ManagementCart managementCart;

    // UI elements
    private ShapeableImageView shapeableImageView;
    private TextView titleTxt, descriptionTxt, priceTxt, numberItemTxt, plusCart, minusCart;
    private RatingBar ratingBar;
    private RecyclerView rvSizeList;
    private Button addToCart;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        managementCart = new ManagementCart(this);
        initView();
        bundle();
        initSizeList();
    }

    private void initView() {
        shapeableImageView = findViewById(R.id.shapeableImageView);
        titleTxt = findViewById(R.id.titleTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        priceTxt = findViewById(R.id.priceTxt);
        numberItemTxt = findViewById(R.id.numberItemTxt);
        ratingBar = findViewById(R.id.ratingBar);
        rvSizeList = findViewById(R.id.rvSizeList);
        addToCart = findViewById(R.id.addToCart);
        ivBack = findViewById(R.id.ivBack);
        plusCart = findViewById(R.id.plusCart);
        minusCart = findViewById(R.id.minusCart);
    }

    private void initSizeList() {
        ArrayList<String> sizeList = new ArrayList<>();
        sizeList.add("1");
        sizeList.add("2");
        sizeList.add("3");
        sizeList.add("4");

        SizeAdapter adapter = new SizeAdapter(this, sizeList);
        rvSizeList.setAdapter(adapter);
        rvSizeList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<String> colorList = new ArrayList<>(item.getPicUrl());

        if (!colorList.isEmpty()) {
            Glide.with(this)
                    .load(colorList.get(0))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(100)))
                    .into(shapeableImageView);
        }
    }

    @SuppressLint("SetTextI18n")
    private void bundle() {
        Intent intent = getIntent();
        item = intent.getParcelableExtra("object");

        if (item != null) {
            titleTxt.setText(item.getTitle());
            descriptionTxt.setText(item.getDescription());
            priceTxt.setText("$" + item.getPrice());
            ratingBar.setRating((float) item.getRating());
            numberItemTxt.setText(String.valueOf(item.getNumberInCart()));

            addToCart.setOnClickListener(v -> {
                item.setNumberInCart(Integer.parseInt(numberItemTxt.getText().toString()));
                managementCart.insertItems(item);
            });

            ivBack.setOnClickListener(v -> finish());

            plusCart.setOnClickListener(v -> {
                int count = item.getNumberInCart() + 1;
                item.setNumberInCart(count);
                numberItemTxt.setText(String.valueOf(count));
            });

            minusCart.setOnClickListener(v -> {
                int count = item.getNumberInCart();
                if (count > 0) {
                    count--;
                    item.setNumberInCart(count);
                    numberItemTxt.setText(String.valueOf(count));
                }
            });
        }
    }
}
