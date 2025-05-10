package com.store.mycoffeestore.model;

import lombok.Data;

@Data
public class Payment {
    private Long paymentId;
    private String paymentMethod;
    private String paymentDescription;
    private String imageUrl;
}

