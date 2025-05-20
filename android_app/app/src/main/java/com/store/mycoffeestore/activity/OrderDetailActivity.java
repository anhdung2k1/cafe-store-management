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
    private TextView statusText;
    private Button deleteOrderBtn;
    private Order order;
    private String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Mapping view
        TextView orderIdText = findViewById(R.id.orderIdText);
        TextView totalText = findViewById(R.id.totalText);
        statusText = findViewById(R.id.statusText);
        TextView customerText = findViewById(R.id.customerText);
        TextView addressText = findViewById(R.id.addressText);
        RadioGroup statusGroup = findViewById(R.id.statusGroup);
        deleteOrderBtn = findViewById(R.id.deleteOrderBtn);
        ImageView backBtn = findViewById(R.id.backBtn);

        // Nhận order từ Intent
        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            Toast.makeText(this, "No order found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set dữ liệu vào view
        orderIdText.setText(String.format("Order # %s", order.getOrderID()));
        totalText.setText(String.format("$%s", order.getTotalAmount()));
        currentStatus = clean(order.getOrderStatus());
        statusText.setText(currentStatus);
        customerText.setText("Customer: Guest");
        addressText.setText("Address: N/A");

        // Check đúng trạng thái
        if (currentStatus.equalsIgnoreCase(SUCCESS)) statusGroup.check(R.id.statusSuccess);
        else if (currentStatus.equalsIgnoreCase(PENDING)) statusGroup.check(R.id.statusPending);
        else if (currentStatus.equalsIgnoreCase(PROCESSING)) statusGroup.check(R.id.statusProcessing);
        else if (currentStatus.equalsIgnoreCase(COMPLETED)) statusGroup.check(R.id.statusCompleted);
        else if (currentStatus.equalsIgnoreCase(CANCELLED)) statusGroup.check(R.id.statusCancelled);

        // Nếu đã thành công, không cho xóa nữa
        if (currentStatus.equalsIgnoreCase(SUCCESS)) {
            deleteOrderBtn.setVisibility(View.GONE);
        }

        // Nút back
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, OrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // Nút xóa đơn
        deleteOrderBtn.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this order?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteOrder())
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        // Cập nhật trạng thái khi chọn RadioButton khác
        statusGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String newStatus = "";
            if (checkedId == R.id.statusSuccess) newStatus = SUCCESS;
            else if (checkedId == R.id.statusPending) newStatus = PENDING;
            else if (checkedId == R.id.statusProcessing) newStatus = PROCESSING;
            else if (checkedId == R.id.statusCompleted) newStatus = COMPLETED;
            else if (checkedId == R.id.statusCancelled) newStatus = CANCELLED;

            if (!newStatus.equalsIgnoreCase(currentStatus)) {
                updateOrderStatus(newStatus);

                // Cập nhật trạng thái hiển thị nút xóa
                if (newStatus.equalsIgnoreCase(SUCCESS)) {
                    deleteOrderBtn.setVisibility(View.GONE);
                } else {
                    deleteOrderBtn.setVisibility(View.VISIBLE);
                }
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
        Call<Map<String,Boolean>> call = api.deleteOrder(order.getOrderID().intValue());

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Boolean>> call, @NonNull Response<Map<String, Boolean>> response) {
                Log.w("OrderDetail", "DeleteOrder response body: " + response.body());
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