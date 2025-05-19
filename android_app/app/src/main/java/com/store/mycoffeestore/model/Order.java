package com.store.mycoffeestore.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Order implements Serializable {
    private Long orderID;
    private String orderStatus;
    private Double totalAmount;
    private String orderDate;
    private Long transactionID;
    private Long userID;
}