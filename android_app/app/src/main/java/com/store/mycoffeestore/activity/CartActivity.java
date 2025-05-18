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

import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.CartAdapter;
import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.api.ApiService;
import com.store.mycoffeestore.helper.TokenManager;
import com.store.mycoffeestore.model.*;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCartView;
    private ImageView ivBack;
    private TextView subTotalPriceTxt, totalTaxPriceTxt, deliveryPriceTxt, totalPriceTxt;
    private EditText addressText;
    private RadioGroup paymentGroup;

    private final List<ItemsModel> cartItems = new ArrayList<>();
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
        addressText = findViewById(R.id.addressText);
        paymentGroup = findViewById(R.id.paymentGroup);

        findViewById(R.id.proceedCheckoutBtn).setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                handleOrderCreationOrUpdate();
            }
        });
    }

    private void setVariable() {
        ivBack.setOnClickListener(view -> {
            Log.d("DEBUG_BACK", "Back button clicked");
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void fetchUserIdAndCart() {
        TokenManager tokenManager = new TokenManager(this);
        String userName = tokenManager.getUserNameFromToken();
        if (userName == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getSecuredApiService(this).getUserIdByUserName(userName)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Long>> call, @NonNull Response<Map<String, Long>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            userId = response.body().get("id");
                            fetchCartItems();
                        } else {
                            Toast.makeText(CartActivity.this, "Cannot get userId", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Long>> call, @NonNull Throwable t) {
                        Toast.makeText(CartActivity.this, "Failed to get userId", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCartItems() {
        ApiClient.getSecuredApiService(this).getCart(userId)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Map<String, Object>> raw = (List<Map<String, Object>>) response.body().get("products");
                            if (raw != null) {
                                cartItems.clear();
                                cartItems.addAll(parseCartItems(raw));
                                setupCartAdapter();
                                calculateCart();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                        Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<ItemsModel> parseCartItems(List<Map<String, Object>> rawList) {
        List<ItemsModel> items = new ArrayList<>();
        for (Map<String, Object> map : rawList) {
            ItemsModel item = new ItemsModel();
            item.setProductID(((Number) Objects.requireNonNull(map.get("productID"))).longValue());
            item.setTitle((String) map.get("productName"));
            item.setDescription((String) map.get("productDescription"));
            item.setPrice(map.get("productPrice") instanceof Number ? ((Number) map.get("productPrice")).doubleValue() : 0.0);
            item.setRating(map.get("rating") instanceof Number ? ((Number) map.get("rating")).floatValue() : 0f);
            item.setExtra((String) map.get("productModel"));
            item.setNumberInCart(map.get("productQuantity") instanceof Number ? ((Number) map.get("productQuantity")).intValue() : 1);

            // Fix ClassCastException by safely converting List<?> to List<String>
            String imageUrl = (String) map.get("imageUrl");
            ArrayList<String> imageList = new ArrayList<>();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageList.add(imageUrl);
            }
            item.setPicUrl(imageList);

            items.add(item);
        }
        return items;
    }


    private void setupCartAdapter() {
        rvCartView.setLayoutManager(new LinearLayoutManager(this));
        rvCartView.setAdapter(new CartAdapter(new ArrayList<>(cartItems), this, userId, this::calculateCart));
    }

    @SuppressLint("SetTextI18n")
    private void calculateCart() {
        double subtotal = 0.0;
        for (ItemsModel item : cartItems) {
            subtotal += item.getPrice() * item.getNumberInCart();
        }
        double tax = Math.round(subtotal * 0.02 * 100) / 100.0;
        double total = Math.round((subtotal + tax + delivery) * 100) / 100.0;
        subtotal = Math.round(subtotal * 100) / 100.0;

        subTotalPriceTxt.setText("$" + subtotal);
        totalTaxPriceTxt.setText("$" + tax);
        deliveryPriceTxt.setText("$" + delivery);
        totalPriceTxt.setText("$" + total);
    }

    private void handleOrderCreationOrUpdate() {
        ApiClient.getSecuredApiService(this).getOrdersByUser(userId)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Order latest = response.body().get(response.body().size() - 1);
                            updateOrderStatus(latest.getOrderID(), "Cart updated");
                        } else {
                            createTransaction();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                        Toast.makeText(CartActivity.this, "Check order failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createTransaction() {
        String address = addressText.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Shipping address required", Toast.LENGTH_SHORT).show();
            return;
        }

        String method = getSelectedPaymentMethod();
        if (method == null) {
            Toast.makeText(this, "Select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        Transactions t = new Transactions();
        t.setTransactionType("Checkout");
        t.setShippingAddress(address);
        t.setBillingPayment(parseTotal(totalPriceTxt));

        Payment p = new Payment();
        p.setPaymentMethod(method);
        t.setPayment(p);

        Log.d("CartActivity", "Transaction data: " + t);

        ApiClient.getSecuredApiService(this).createTransaction(userId, t)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                        if (Boolean.TRUE.equals(response.body())) {
                            Toast.makeText(CartActivity.this, "Transaction complete!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CartActivity.this, "Failed to create transaction", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                        Log.e("CartActivity", "createTransaction failed", t);
                        Toast.makeText(CartActivity.this, "Transaction error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSelectedPaymentMethod() {
        int id = paymentGroup.getCheckedRadioButtonId();
        if (id == R.id.creditCard) return "Credit Card";
        if (id == R.id.cash) return "Cash";
        if (id == R.id.momo) return "MOMO";
        if (id == R.id.paypal) return "Paypal";
        return null;
    }

    private double parseTotal(TextView tv) {
        try {
            return Double.parseDouble(tv.getText().toString().replace("$", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void updateOrderStatus(Long orderId, String status) {
        ApiClient.getSecuredApiService(this).updateOrder(orderId, status)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                        Toast.makeText(CartActivity.this, "Order updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                        Toast.makeText(CartActivity.this, "Failed to update order", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}