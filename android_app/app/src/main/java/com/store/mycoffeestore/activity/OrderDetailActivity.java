package com.store.mycoffeestore.activity;

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

    private TextView orderIdText, totalText, statusText, customerText, addressText;
    private RadioGroup statusGroup;
    private Button deleteOrderBtn;
    private ImageView backBtn;

    private Order order;
    private String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Mapping view
        orderIdText = findViewById(R.id.orderIdText);
        totalText = findViewById(R.id.totalText);
        statusText = findViewById(R.id.statusText);
        customerText = findViewById(R.id.customerText);
        addressText = findViewById(R.id.addressText);
        statusGroup = findViewById(R.id.statusGroup);
        deleteOrderBtn = findViewById(R.id.deleteOrderBtn);
        backBtn = findViewById(R.id.backBtn);

        // Nhận order từ Intent
        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            Toast.makeText(this, "No order found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set dữ liệu vào view
        orderIdText.setText("Order #" + order.getOrderID());
        totalText.setText(String.format("$%.2f", order.getTotalAmount()));
        currentStatus = clean(order.getOrderStatus());
        statusText.setText(currentStatus);
        customerText.setText("Customer: Guest");
        addressText.setText("Address: N/A");

        // Check đúng trạng thái
        if (currentStatus.equalsIgnoreCase("Success")) statusGroup.check(R.id.statusSuccess);
        else if (currentStatus.equalsIgnoreCase("Pending")) statusGroup.check(R.id.statusPending);
        else if (currentStatus.equalsIgnoreCase("Processing")) statusGroup.check(R.id.statusProcessing);
        else if (currentStatus.equalsIgnoreCase("Completed")) statusGroup.check(R.id.statusCompleted);
        else if (currentStatus.equalsIgnoreCase("Cancelled")) statusGroup.check(R.id.statusCancelled);

        // Nếu đã thành công, không cho xóa nữa
        if (currentStatus.equalsIgnoreCase("Success")) {
            deleteOrderBtn.setVisibility(View.GONE);
        }

        // Nút back
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, OrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // Nút xóa đơn
        deleteOrderBtn.setOnClickListener(v -> deleteOrder());

        // Cập nhật trạng thái khi chọn RadioButton khác
        statusGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String newStatus = "";
            if (checkedId == R.id.statusSuccess) newStatus = "Success";
            else if (checkedId == R.id.statusPending) newStatus = "Pending";
            else if (checkedId == R.id.statusProcessing) newStatus = "Processing";
            else if (checkedId == R.id.statusCompleted) newStatus = "Completed";
            else if (checkedId == R.id.statusCancelled) newStatus = "Cancelled";

            if (!newStatus.equalsIgnoreCase(currentStatus)) {
                updateOrderStatus(newStatus);
            }
        });
    }

    private void updateOrderStatus(String newStatus) {
        ApiService api = ApiClient.getSecuredApiService(this);
        Call<Map<String, Object>> call = api.updateOrderStatus(order.getOrderID().intValue(), "\"" + newStatus + "\"");

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OrderDetailActivity.this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    currentStatus = newStatus;
                    statusText.setText(newStatus);
                    if (newStatus.equalsIgnoreCase("Success")) {
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
        Call<Boolean> call = api.deleteOrder(order.getOrderID().intValue());

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (Boolean.TRUE.equals(response.body())) {
                    Toast.makeText(OrderDetailActivity.this, "Order deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to delete order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderDetail", "deleteOrder failed", t);
            }
        });
    }

    private String clean(String raw) {
        return raw != null ? raw.replace("\"", "") : "Unknown";
    }
}