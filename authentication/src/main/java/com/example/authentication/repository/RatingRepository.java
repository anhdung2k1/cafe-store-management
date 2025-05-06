package com.example.authentication.repository;

import com.example.authentication.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

    @Query("SELECT p.rate FROM ProductEntity p WHERE p.productID = :productID")
    Optional<RatingEntity> findRatingEntityWithProductID(@Param("productID") Long productID);
}