package com.example.authentication.model;

import com.example.authentication.entity.RatingEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long productID;
    private String productName;
    private String productModel;
    private String productType;
    private String productDescription;
    private Integer productQuantity;
    private Double productPrice;
    private String imageUrl;
    private RatingEntity rate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
