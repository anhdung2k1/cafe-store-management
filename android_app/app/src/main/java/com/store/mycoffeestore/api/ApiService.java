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

    /**
     * Retrieves a list of all orders.
     *
     * @return a Call object for a list of Order instances representing all orders in the system
     */
    @GET("orders")
    Call<List<Order>> getAllOrders();

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId the unique identifier of the user whose orders are to be fetched
     * @return a Call object for a list of Order objects associated with the user
     */
    @GET("orders/{userID}")
    Call<List<Order>> getOrdersByUser(@Path("userID") Long userId);

    /**
     * Retrieves the details of a specific order by its ID.
     *
     * @param orderId the unique identifier of the order to retrieve
     * @return a Call object for the requested Order
     */
    @GET("orders/order/{orderID}")
    Call<Order> getOrderById(@Path("orderID") Long orderId);

    /**
     * Updates the status of an existing order.
     *
     * @param orderId the unique identifier of the order to update
     * @param status the new status to set for the order
     * @return a Call object for the updated Order
     */
    @PATCH("orders/order/{orderID}")
    Call<Order> updateOrder(@Path("orderID") Long orderId, @Body String status);

    /**
     * Deletes an order by its ID.
     *
     * @param orderId the unique identifier of the order to delete
     * @return a map indicating whether the deletion was successful
     */
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