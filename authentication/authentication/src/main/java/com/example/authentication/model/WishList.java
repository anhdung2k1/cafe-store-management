package com.example.authentication.model;

import com.example.authentication.entity.ProductEntity;
import com.example.authentication.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishList {
    private Long wishListId;
    private List<ProductEntity> products;
    private UserEntity user;
}
