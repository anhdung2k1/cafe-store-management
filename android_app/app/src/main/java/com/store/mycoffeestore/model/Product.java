package com.store.mycoffeestore.model;

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
}