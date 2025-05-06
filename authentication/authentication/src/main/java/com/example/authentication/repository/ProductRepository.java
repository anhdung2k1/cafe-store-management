package com.example.authentication.repository;

import com.example.authentication.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<ProductEntity> findAllByProductNameContains(String productName);

    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.productType) LIKE LOWER(CONCAT('%', :productType, '%'))")
    List<ProductEntity> findAllByProductTypeContains(String productType);

    @Query("SELECT p FROM ProductEntity p")
    List<ProductEntity> findAllProducts(); // Add pagination in the service layer if needed

    @Query("SELECT DISTINCT p.productType FROM ProductEntity p")
    List<String> findProductCategories();
}