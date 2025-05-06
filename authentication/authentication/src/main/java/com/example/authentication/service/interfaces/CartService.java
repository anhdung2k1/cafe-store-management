package com.example.authentication.service.interfaces;

import com.example.authentication.model.Product;

import java.util.Map;

public interface CartService {
    Map<String, Object> getCartItems(Long userId) throws Exception;
    Map<String, Object> addCartItems(Long userId, Product product) throws Exception;
    Map<String, Object> updateCartItems(Long userId, Product product) throws Exception;
}
