package com.example.authentication.model;

import com.example.authentication.entity.TransactionEntity;
import com.example.authentication.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long orderID;
    private UserEntity user;
    private TransactionEntity transaction;
    private String orderDate;
    private String orderStatus;
    private Double totalAmount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
