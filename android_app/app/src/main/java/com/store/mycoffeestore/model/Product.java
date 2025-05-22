package com.store.mycoffeestore.model;

import java.util.Map;
import java.util.Objects;

import lombok.Data;

@Data
public class Product {
    private Long productID;
    private String productName;
    private String productModel;
    private String productType;
    private String productDescription;
    private String imageUrl;
    private double productPrice;
    private int productQuantity;

    public static Product fromMap(Map<String, Object> map) {
        Product product = new Product();

        if (map.get("productID") != null)
            product.setProductID(((Number) Objects.requireNonNull(map.get("productID"))).longValue());

        product.setProductName((String) map.get("productName"));
        product.setProductModel((String) map.get("productModel"));
        product.setProductType((String) map.get("productType"));
        product.setProductDescription((String) map.get("productDescription"));
        product.setImageUrl((String) map.get("imageUrl"));

        if (map.get("productPrice") != null)
            product.setProductPrice(Double.parseDouble(Objects.requireNonNull(map.get("productPrice")).toString()));

        if (map.get("productQuantity") != null)
            product.setProductQuantity(((Number) Objects.requireNonNull(map.get("productQuantity"))).intValue());

        return product;
    }
}