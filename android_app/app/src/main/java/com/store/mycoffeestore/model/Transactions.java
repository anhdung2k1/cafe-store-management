package com.store.mycoffeestore.model;
import lombok.Data;

@Data
public class Transactions {
    private String transactionType;
    private String shippingAddress;
    private double billingPayment;
    private Payment payment;
}
