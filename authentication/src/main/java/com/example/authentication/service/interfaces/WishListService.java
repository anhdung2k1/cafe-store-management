package com.example.authentication.service.interfaces;

import com.example.authentication.model.Product;

import java.util.Map;

public interface WishListService {
    Map<String, Object> getWishListItems(Long userId) throws Exception;
    Map<String, Object> addWishListItems(Long userId, Product product) throws Exception;
    Map<String, Object> removeWishListItems(Long userId, Product product) throws Exception;
    Boolean removeAllWishListItems(Long userId) throws Exception;
}