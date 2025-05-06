package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "PRODUCT")
@Transactional(rollbackOn = Exception.class)
public class ProductEntity {
    public ProductEntity() {
        this.productName = "";
        this.productModel = "";
        this.productType = "";
        this.productDescription = "";
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID", nullable = false, unique = true)
    @TableGenerator(name = "PRODUCT_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "PRODUCT_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long productID;

    @Column(name = "PRODUCT_NAME", nullable = false)
    @NotBlank(message = "Product Name must not be blank")
    private String productName;

    @Column(name = "PRODUCT_MODEL")
    private String productModel;

    @Column(name = "PRODUCT_TYPE", nullable = false)
    @NotBlank(message = "Product Type must not be blank")
    private String productType;

    @Column(name = "PRODUCT_QUANT", nullable = false)
    private Integer productQuantity;

    @Column(name = "PRODUCT_PRICE", nullable = false)
    @NotNull(message = "Must specify price")
    @Positive(message = "Price must be greater than zero")
    private Double productPrice;

    @Column(name = "PRODUCT_DESC")
    private String productDescription;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CART_ID")
    private CartEntity cart;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RATING_ID")
    private RatingEntity rate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;
}
