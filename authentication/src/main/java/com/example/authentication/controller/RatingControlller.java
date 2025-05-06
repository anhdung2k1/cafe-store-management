package com.example.authentication.controller;

import com.example.authentication.model.Rating;
import com.example.authentication.service.interfaces.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RatingControlller {
    private final RatingService ratingService;
    public RatingControlller(RatingService ratingService) {
        this.ratingService = ratingService;
    }
    @PostMapping(value = "/ratings")
    public ResponseEntity<Boolean> createRating(@RequestBody Rating rating) throws Exception {
        return ResponseEntity.ok(ratingService.createRating(rating));
    }
    // Get rating with Product ID
    @GetMapping(value = "/ratings/products/{productID}")
    public ResponseEntity<Map<String, Object>> getRatingWithProductID(@PathVariable Long productID) throws Exception {
        return ResponseEntity.ok(ratingService.getRatingsWithProductID(productID));
    }
    // Update rating with Product ID
    @PatchMapping(value = "/ratings/products/{productID}")
    public ResponseEntity<Boolean> updateRatingWithProductID(@PathVariable Long productID, @RequestBody Rating rating) throws Exception {
        return ResponseEntity.ok(ratingService.updateRatingWithProductID(rating, productID));
    }
}
