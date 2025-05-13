package com.store.mycoffeestore.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.CartAdapter;
import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.api.ApiService;
import com.store.mycoffeestore.helper.TokenManager;
import com.store.mycoffeestore.model.ItemsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCartView;
    private ImageView ivBack;
    private TextView subTotalPriceTxt, totalTaxPriceTxt, deliveryPriceTxt, totalPriceTxt;
    private EditText etCoupon;

    private List<ItemsModel> cartItems = new ArrayList<>();
    private final double delivery = 15.0;

    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setVariable();
        fetchUserIdAndCart();
    }

    private void initViews() {
        rvCartView = findViewById(R.id.rvCartView);
        ivBack = findViewById(R.id.ivBack);
        subTotalPriceTxt = findViewById(R.id.subTotalPriceTxt);
        totalTaxPriceTxt = findViewById(R.id.totalTaxPriceTxt);
        deliveryPriceTxt = findViewById(R.id.deliveryPriceTxt);
        totalPriceTxt = findViewById(R.id.totalPriceTxt);
        etCoupon = findViewById(R.id.etCoupen);
    }

    private void setVariable() {
        ivBack.setOnClickListener(view -> finish());
    }

    private void fetchUserIdAndCart() {
        TokenManager tokenManager = new TokenManager(this);
        String userName = tokenManager.getUserNameFromToken();

        if (userName == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getSecuredApiService(this);
        api.getUserIdByUserName(userName).enqueue(new Callback<Map<String, Long>>() {
            @Override
            public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body().get("id");
                    fetchCartItems();
                } else {
                    Toast.makeText(CartActivity.this, "Cannot get userId", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Long>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Failed to get userId", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCartItems() {
        ApiService api = ApiClient.getSecuredApiService(this);
        api.getCart(userId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> rawProducts = (List<Map<String, Object>>) response.body().get("products");
                    cartItems = parseCartItems(rawProducts);
                    setupCartAdapter();
                    calculateCart();
                } else {
                    Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CartActivity", "onFailure: ", t);
            }
        });
    }

    private List<ItemsModel> parseCartItems(List<Map<String, Object>> rawList) {
        List<ItemsModel> items = new ArrayList<>();
        for (Map<String, Object> map : rawList) {
            Log.d("DEBUG_CART_ITEM", "map keys: " + map.keySet());
            ItemsModel item = new ItemsModel();
            item.setProductID(((Number) Objects.requireNonNull(map.get("productID"))).longValue());
            item.setTitle((String) map.get("productName"));
            item.setDescription((String) map.get("productDescription"));

            Object priceObj = map.get("productPrice");
            item.setPrice(priceObj instanceof Number ? ((Number) priceObj).doubleValue() : 0.0);

            Object ratingObj = map.get("rating");
            item.setRating(ratingObj instanceof Number ? ((Number) ratingObj).floatValue() : 0f);

            item.setExtra((String) map.get("productModel"));

            Object quantity = map.getOrDefault("productQuantity", 1);
            item.setNumberInCart(quantity instanceof Number ? ((Number) quantity).intValue() : 1);

            ArrayList<String> pic = new ArrayList<>();
            if (map.containsKey("imageUrl")) {
                pic.add((String) map.get("imageUrl"));
            }
            item.setPicUrl(pic);

            items.add(item);
        }
        return items;
    }

    private void setupCartAdapter() {
        rvCartView.setLayoutManager(new LinearLayoutManager(this));
        rvCartView.setAdapter(new CartAdapter(
                new ArrayList<>(cartItems),
                this,
                userId,
                this::calculateCart
        ));
    }

    @SuppressLint("SetTextI18n")
    private void calculateCart() {
        double subtotal = 0.0;
        for (ItemsModel item : cartItems) {
            subtotal += item.getPrice() * item.getNumberInCart();
        }

        double percentTax = 0.02;
        double tax = Math.round((subtotal * percentTax) * 100) / 100.0;
        double total = Math.round((subtotal + tax + delivery) * 100) / 100.0;
        subtotal = Math.round(subtotal * 100) / 100.0;

        subTotalPriceTxt.setText("$" + subtotal);
        totalTaxPriceTxt.setText("$" + tax);
        deliveryPriceTxt.setText("$" + delivery);
        totalPriceTxt.setText("$" + total);
    }
}