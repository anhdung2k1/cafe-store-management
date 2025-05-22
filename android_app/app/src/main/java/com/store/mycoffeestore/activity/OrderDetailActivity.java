package com.store.mycoffeestore.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.store.mycoffeestore.R;
import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.api.ApiService;
import com.store.mycoffeestore.model.Order;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private final String SUCCESS = "SUCCESS";
    private final String PENDING = "PENDING";
    private final String PROCESSING = "PROCESSING";
    private final String COMPLETED = "COMPLETED";
    private final String CANCELLED = "CANCELLED";

    private TextView statusText, orderIdText, totalText, customerText, addressText;
    private RadioGroup statusGroup;
    private Button deleteOrderBtn;
    private ImageView backBtn;

    private Order order;
    private String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Ánh xạ view
        orderIdText = findViewById(R.id.orderIdText);
        totalText = findViewById(R.id.totalText);
        statusText = findViewById(R.id.statusText);
        customerText = findViewById(R.id.customerText);
        addressText = findViewById(R.id.addressText);
        statusGroup = findViewById(R.id.statusGroup);
        deleteOrderBtn = findViewById(R.id.deleteOrderBtn);
        backBtn = findViewById(R.id.backBtn);

        long orderId = getIntent().getLongExtra("orderID", -1L);
        if (orderId == -1L) {
            Toast.makeText(this, "Order ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadOrderDetail(orderId);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, OrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void loadOrderDetail(long orderId) {
        ApiService api = ApiClient.getSecuredApiService(this);
        api.getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    order = response.body();
                    populateOrderDetails();
                    loadUserDetail(order.getUserID()); // Gọi thêm user
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Failed to load order", Toast.LENGTH_SHORT).show();
                Log.e("OrderDetail", "loadOrderDetail failed", t);
                finish();
            }
        });
    }

    private void loadUserDetail(Long userId) {
        if (userId == null) return;

        ApiService api = ApiClient.getSecuredApiService(this);
        api.getUserById(userId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String userName = (String) response.body().get("userName");
                    String address = (String) response.body().get("address");

                    customerText.setText("Customer: " + (userName != null ? userName : "Guest"));
                    addressText.setText("Address: " + (address != null ? address : "N/A"));
                } else {
                    customerText.setText("Customer: Guest");
                    addressText.setText("Address: N/A");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                customerText.setText("Customer: Guest");
                addressText.setText("Address: N/A");
                Log.e("OrderDetail", "loadUserDetail failed", t);
            }
        });
    }

    private void populateOrderDetails() {
        currentStatus = clean(order.getOrderStatus());

        orderIdText.setText(String.format("Order # %s", order.getOrderID()));
        totalText.setText(String.format("$%s", order.getTotalAmount()));
        statusText.setText(currentStatus);

        if (currentStatus.equalsIgnoreCase(SUCCESS)) statusGroup.check(R.id.statusSuccess);
        else if (currentStatus.equalsIgnoreCase(PENDING)) statusGroup.check(R.id.statusPending);
        else if (currentStatus.equalsIgnoreCase(PROCESSING)) statusGroup.check(R.id.statusProcessing);
        else if (currentStatus.equalsIgnoreCase(COMPLETED)) statusGroup.check(R.id.statusCompleted);
        else if (currentStatus.equalsIgnoreCase(CANCELLED)) statusGroup.check(R.id.statusCancelled);

        deleteOrderBtn.setVisibility(currentStatus.equalsIgnoreCase(SUCCESS) ? View.GONE : View.VISIBLE);

        deleteOrderBtn.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this order?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteOrder())
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        statusGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String newStatus = "";
            if (checkedId == R.id.statusSuccess) newStatus = SUCCESS;
            else if (checkedId == R.id.statusPending) newStatus = PENDING;
            else if (checkedId == R.id.statusProcessing) newStatus = PROCESSING;
            else if (checkedId == R.id.statusCompleted) newStatus = COMPLETED;
            else if (checkedId == R.id.statusCancelled) newStatus = CANCELLED;

            if (!newStatus.equalsIgnoreCase(currentStatus)) {
                updateOrderStatus(newStatus);
                deleteOrderBtn.setVisibility(newStatus.equalsIgnoreCase(SUCCESS) ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void updateOrderStatus(String newStatus) {
        ApiService api = ApiClient.getSecuredApiService(this);
        Call<Map<String, Object>> call = api.updateOrderStatus(order.getOrderID().intValue(), clean(newStatus));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OrderDetailActivity.this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    currentStatus = newStatus;
                    statusText.setText(newStatus);
                    if (newStatus.equalsIgnoreCase(SUCCESS)) {
                        deleteOrderBtn.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderDetail", "updateOrderStatus failed", t);
            }
        });
    }

    private void deleteOrder() {
        ApiService api = ApiClient.getSecuredApiService(this);
        Call<Map<String, Boolean>> call = api.deleteOrder(order.getOrderID().intValue());

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Boolean>> call, @NonNull Response<Map<String, Boolean>> response) {
                if (response.body() != null && Boolean.TRUE.equals(response.body().get("deleted"))) {
                    Toast.makeText(OrderDetailActivity.this, "Order deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to delete order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Boolean>> call, @NonNull Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderDetail", "deleteOrder failed", t);
            }
        });
    }

    private String clean(String raw) {
        return raw != null ? raw.replace("\"", "") : "Unknown";
    }
}