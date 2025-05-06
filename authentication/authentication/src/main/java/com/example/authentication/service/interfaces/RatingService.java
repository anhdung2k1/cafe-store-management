package com.example.authentication.service.interfaces;

import com.example.authentication.model.Rating;

import java.util.Map;

public interface RatingService {
    Boolean createRating(Rating rating) throws Exception;
    Map<String, Object> getRatingsWithProductID(Long productID) throws Exception;
    Boolean updateRatingWithProductID(Rating rating, Long mobileID) throws Exception;
}
