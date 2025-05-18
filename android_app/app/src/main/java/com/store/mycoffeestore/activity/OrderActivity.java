package com.store.mycoffeestore.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.OrderAdapter;
import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.api.ApiService;
import com.store.mycoffeestore.helper.NavigationHelper;
import com.store.mycoffeestore.helper.TokenManager;
import com.store.mycoffeestore.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView orderRecycler;
    private final List<Order> orders = new ArrayList<>();
    private OrderAdapter adapter;
    private Long userId;

    /**
     * Initializes the activity, sets up the order list UI, configures navigation, and begins loading user orders.
     *
     * @param savedInstanceState the previously saved state of the activity, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderRecycler = findViewById(R.id.orderRecycler);
        orderRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, orders);
        orderRecycler.setAdapter(adapter);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigation(this, nav, R.id.order_btn);

        fetchUserIdThenOrders();
    }

    /**
     * Retrieves the current user's ID using their username and loads their orders.
     *
     * If the user is not logged in, displays a message and aborts the process. Otherwise, fetches the user ID asynchronously and, upon success, initiates loading of the user's orders. Displays error messages if user ID retrieval fails.
     */
    private void fetchUserIdThenOrders() {
        TokenManager tokenManager = new TokenManager(this);
        String userName = tokenManager.getUserNameFromToken();

        if (userName == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getSecuredApiService(this);
        api.getUserIdByUserName(userName).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Long>> call, @NonNull Response<Map<String, Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body().get("id");
                    loadOrders(userId);
                } else {
                    Toast.makeText(OrderActivity.this, "Failed to get user ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Long>> call, @NonNull Throwable t) {
                Toast.makeText(OrderActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Retrieves and displays the list of orders for the specified user.
     *
     * Initiates an asynchronous API call to fetch orders associated with the given user ID.
     * Updates the orders list and refreshes the RecyclerView upon successful retrieval.
     * Shows a toast message if the request fails or returns no data.
     *
     * @param userId the ID of the user whose orders are to be loaded
     */
    private void loadOrders(Long userId) {
        ApiService api = ApiClient.getSecuredApiService(this);
        api.getOrdersByUser(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders.clear();
                    orders.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(OrderActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Toast.makeText(OrderActivity.this, "Error loading orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}