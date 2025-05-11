package com.store.mycoffeestore.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.SizeAdapter;
import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.helper.SizeSelectListener;
import com.store.mycoffeestore.helper.TokenManager;
import com.store.mycoffeestore.model.ItemsModel;
import com.store.mycoffeestore.model.Product;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailedActivity extends AppCompatActivity {

    private ItemsModel item;
    private Long userId;
    private String selectedSize = "1"; // default

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

        initView();
        bundle();
        initSizeList();
        fetchUserId();
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

        SizeAdapter adapter = new SizeAdapter(this, sizeList, new SizeSelectListener() {
            @Override
            public void onSizeSelected(String size) {
                selectedSize = size;
            }
        });

        rvSizeList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSizeList.setAdapter(adapter);

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
        item = intent.getParcelableExtra("object", ItemsModel.class);

        if (item != null) {
            titleTxt.setText(item.getTitle());
            descriptionTxt.setText(item.getDescription());
            priceTxt.setText("$" + item.getPrice());
            ratingBar.setRating(item.getRating());
            numberItemTxt.setText(String.valueOf(item.getNumberInCart()));

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

            addToCart.setOnClickListener(v -> {
                item.setNumberInCart(Integer.parseInt(numberItemTxt.getText().toString()));
                addItemToCart();
            });
        }
    }

    private void fetchUserId() {
        TokenManager tokenManager = new TokenManager(this);
        String userName = tokenManager.getUserNameFromToken();

        if (userName == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getSecuredApiService(this).getUserIdByUserName(userName)
                .enqueue(new Callback<Map<String, Long>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Long>> call, @NonNull Response<Map<String, Long>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            userId = response.body().get("id");
                        } else {
                            Toast.makeText(DetailedActivity.this, "Cannot get userId", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Long>> call, @NonNull Throwable t) {
                        Toast.makeText(DetailedActivity.this, "Failed to get userId", Toast.LENGTH_SHORT).show();
                        Log.e("DetailedActivity", "userId error", t);
                    }
                });
    }

    private void addItemToCart() {
        if (userId == null) {
            Toast.makeText(this, "User ID not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Product product = getProduct();

        ApiClient.getSecuredApiService(this)
                .addToCart(userId, product)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(DetailedActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DetailedActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                        Toast.makeText(DetailedActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        Log.e("AddToCart", "Error: ", t);
                    }
                });
    }

    @NonNull
    private Product getProduct() {
        Product product = new Product();
        product.setProductName(item.getTitle());
        product.setProductModel(selectedSize);
        product.setProductPrice(item.getPrice());
        product.setProductQuantity(item.getNumberInCart());
        product.setProductDescription(item.getDescription());
        product.setProductType("Popular");

        if (!item.getPicUrl().isEmpty()) {
            product.setImageUrl(item.getPicUrl().get(0));
        }

        return product;
    }
}