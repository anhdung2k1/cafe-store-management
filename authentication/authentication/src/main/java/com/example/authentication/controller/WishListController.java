package com.example.authentication.controller;

import com.example.authentication.model.Product;
import com.example.authentication.service.interfaces.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class WishListController {
    private final WishListService wishListService;
    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }
    @GetMapping(value = "/wishlist/user/{userId}")
    public ResponseEntity<Map<String, Object>> getWishListItemsByUserId(@PathVariable Long userId) throws Exception {
        return ResponseEntity.ok(wishListService.getWishListItems(userId));
    }
    @PostMapping(value = "/wishlist/user/{userId}")
    public ResponseEntity<Map<String, Object>> createWishListItems(@PathVariable Long userId, @RequestBody Product product) throws Exception {
        return ResponseEntity.ok(wishListService.addWishListItems(userId, product));
    }
    @PatchMapping(value = "/wishlist/user/{userId}")
    public ResponseEntity<Map<String, Object>> removeWishListItems(@PathVariable Long userId, @RequestBody Product product) throws Exception {
        return ResponseEntity.ok(wishListService.removeWishListItems(userId, product));
    }
    @PatchMapping(value = "/wishlist/user/{userId}/remove")
    public ResponseEntity<Boolean> removeAllWishListItems(@PathVariable Long userId) throws Exception {
        return ResponseEntity.ok(wishListService.removeAllWishListItems(userId));
    }
}