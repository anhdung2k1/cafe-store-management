package com.store.mycoffeestore.model;

import lombok.Data;

@Data
public class Order {
    private Long orderID;
    private String orderStatus;
    private Double totalAmount;
    private String orderDate;
    private Long transactionID;
    private Long userID;
}