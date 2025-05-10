package com.store.mycoffeestore.api;

import com.store.mycoffeestore.model.*;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ===== ACCOUNT =====
    @POST("accounts/signup")
    Call<AuthenticationResponse> signUp(@Body Accounts account);

    @POST("accounts/signin")
    Call<AuthenticationResponse> signIn(@Body Accounts account);

    // ===== PRODUCTS =====
    @GET("products")
    Call<List<Map<String, Object>>> getAllProducts();

    @GET("products/query")
    Call<List<Map<String, Object>>> getProductsByName(@Query("query") String name);

    @GET("products/types/query")
    Call<List<Map<String, Object>>> getProductsByType(@Query("query") String type);

    @GET("products/categories")
    Call<List<String>> getProductCategories();

    @GET("products/{productId}")
    Call<Map<String, Object>> getProductById(@Path("productId") Long id);

    // ===== CART =====
    @GET("carts/user/{userId}")
    Call<Map<String, Object>> getCart(@Path("userId") Long userId);

    @POST("carts/user/{userId}")
    Call<Map<String, Object>> addToCart(@Path("userId") Long userId, @Body Product product);

    @PATCH("carts/user/{userId}")
    Call<Map<String, Object>> updateCart(@Path("userId") Long userId, @Body Product product);

    // ===== WISHLIST =====
    @GET("wishlist/user/{userId}")
    Call<Map<String, Object>> getWishlist(@Path("userId") Long userId);

    @POST("wishlist/user/{userId}")
    Call<Map<String, Object>> addToWishlist(@Path("userId") Long userId, @Body Product product);

    @PATCH("wishlist/user/{userId}")
    Call<Map<String, Object>> removeFromWishlist(@Path("userId") Long userId, @Body Product product);

    @PATCH("wishlist/user/{userId}/remove")
    Call<Boolean> clearWishlist(@Path("userId") Long userId);

    // ===== ORDERS =====
    @GET("orders")
    Call<List<Map<String, Object>>> getAllOrders();

    @GET("orders/{userID}")
    Call<List<Map<String, Object>>> getOrdersByUser(@Path("userID") Long userId);

    @GET("orders/order/{orderID}")
    Call<Map<String, Object>> getOrderById(@Path("orderID") Long orderId);

    @PATCH("orders/order/{orderID}")
    Call<Map<String, Object>> updateOrder(@Path("orderID") Long orderId, @Body String status);

    @DELETE("orders/order/{orderID}")
    Call<Map<String, Boolean>> deleteOrder(@Path("orderID") Long orderId);

    // ===== PAYMENTS =====
    @GET("payments")
    Call<List<Map<String, Object>>> getAllPayments();

    @GET("payments/user/{userId}")
    Call<List<Map<String, Object>>> getPaymentsByUser(@Path("userId") Long userId);

    @POST("payments")
    Call<Map<String, Object>> createPayment(@Body Payment payment);

    // ===== TRANSACTIONS =====
    @GET("transactions/user/{userId}")
    Call<List<Map<String, Object>>> getTransactionsByUser(@Path("userId") Long userId);

    @POST("transactions/{userId}")
    Call<Boolean> createTransaction(@Path("userId") Long userId, @Body Transactions transactions);

    @GET("transactions/{transactionId}")
    Call<Map<String, Object>> getTransactionById(@Path("transactionId") Long id);

    // ===== USERS =====
    @GET("users/{id}")
    Call<Map<String, Object>> getUserById(@Path("id") Long id);

    @GET("users/search")
    Call<List<Map<String, Object>>> getUserByName(@Query("userName") String name);

    @GET("users/find")
    Call<Map<String, Long>> getUserIdByUserName(@Query("userName") String userName);

    @POST("users")
    Call<Users> createUser(@Body Users user);

    @PATCH("users/{id}")
    Call<Users> updateUser(@Path("id") Long id, @Body Users user);

    @DELETE("users/{id}")
    Call<Map<String, Boolean>> deleteUser(@Path("id") Long id);
}