package com.store.mycoffeestore.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.WishlistAdapter;
import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.api.ApiService;
import com.store.mycoffeestore.helper.NavigationHelper;
import com.store.mycoffeestore.helper.TokenManager;
import com.store.mycoffeestore.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistActivity extends AppCompatActivity {

    private static final String TAG = "WishlistActivity";

    private RecyclerView wishlistRecycler;
    private WishlistAdapter adapter;
    private List<Product> wishlist = new ArrayList<>();
    private ApiService api;
    private Long userId;

    private LinearLayout emptyLayout;
    private ImageView clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // Ánh xạ view
        wishlistRecycler = findViewById(R.id.wishlistRecycler);
        emptyLayout = findViewById(R.id.emptyLayout);
        clearBtn = findViewById(R.id.clearWishlistBtn);

        wishlistRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WishlistAdapter(this, wishlist, this::removeFromWishlist);
        wishlistRecycler.setAdapter(adapter);

        api = ApiClient.getSecuredApiService(this);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigation(this, nav, R.id.wishlist_btn);

        clearBtn.setOnClickListener(v -> {
            Log.w(TAG, "Clear wishlist button clicked");
            clearWishlist();
        });

        Log.w(TAG, "Fetching user ID...");
        fetchUserIdAndLoadWishlist();
    }

    private void fetchUserIdAndLoadWishlist() {
        TokenManager tokenManager = new TokenManager(this);
        String userName = tokenManager.getUserNameFromToken();
        Log.w(TAG, "Username from token: " + userName);

        api.getUserIdByUserName(userName).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Long>> call, @NonNull Response<Map<String, Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body().get("id");
                    Log.w(TAG, "User ID fetched: " + userId);
                    loadWishlist();
                } else {
                    Log.w(TAG, "Failed to get user ID from response");
                    Toast.makeText(WishlistActivity.this, "Failed to get user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Long>> call, @NonNull Throwable t) {
                Log.w(TAG, "Error getting user ID", t);
                Toast.makeText(WishlistActivity.this, "Failed to get user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWishlist() {
        Log.w(TAG, "Calling API to load wishlist...");
        api.getWishlist(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.w(TAG, "Wishlist loaded from API");
                    List<Map<String, Object>> productMaps = (List<Map<String, Object>>) response.body().get("products");

                    wishlist.clear();
                    for (Map<String, Object> productMap : productMaps) {
                        Product product = Product.fromMap(productMap);
                        wishlist.add(product);
                        Log.w(TAG, "Added to list: " + product.getProductName());
                    }

                    adapter.updateList(wishlist);
                    Log.w(TAG, "Adapter updated with " + wishlist.size() + " products");

                    updateUIBasedOnData();
                } else {
                    Log.w(TAG, "API returned no products");
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Log.w(TAG, "API call failed when loading wishlist", t);
                Toast.makeText(WishlistActivity.this, "Failed to load wishlist", Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void removeFromWishlist(Product product) {
        Log.w(TAG, "Removing product: " + product.getProductName());

        api.removeFromWishlist(userId, product).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    wishlist.remove(product);
                    adapter.updateList(wishlist);
                    Toast.makeText(WishlistActivity.this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Product removed: " + product.getProductName());

                    updateUIBasedOnData();
                } else {
                    Log.w(TAG, "Failed to remove product: " + product.getProductID());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Log.w(TAG, "Failed to remove from wishlist", t);
                Toast.makeText(WishlistActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearWishlist() {
        Log.w(TAG, "Calling API to clear wishlist...");
        api.clearWishlist(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (Boolean.TRUE.equals(response.body())) {
                    wishlist.clear();
                    adapter.updateList(wishlist);
                    Toast.makeText(WishlistActivity.this, "Wishlist cleared", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Wishlist cleared");

                    updateUIBasedOnData();
                } else {
                    Log.w(TAG, "Wishlist not cleared - server response failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.w(TAG, "Failed to clear wishlist", t);
                Toast.makeText(WishlistActivity.this, "Clear failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIBasedOnData() {
        if (wishlist.isEmpty()) {
            wishlistRecycler.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            wishlistRecycler.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    private void showEmptyState() {
        wishlistRecycler.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }
}
