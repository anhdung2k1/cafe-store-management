package com.example.authentication.controller;

import com.example.authentication.model.Product;
import com.example.authentication.service.interfaces.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @GetMapping(value = "/carts/user/{userId}")
    public ResponseEntity<Map<String, Object>> getCartItemsByUserId(@PathVariable Long userId) throws Exception {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }
    @PostMapping(value = "/carts/user/{userId}")
    public ResponseEntity<Map<String, Object>> createCartItems(@PathVariable Long userId, @RequestBody Product product) throws Exception {
        return ResponseEntity.ok(cartService.addCartItems(userId, product));
    }
    @PatchMapping(value = "/carts/user/{userId}")
    public ResponseEntity<Map<String, Object>> updateCartItems(@PathVariable Long userId, @RequestBody Product product) throws Exception {
        return ResponseEntity.ok(cartService.updateCartItems(userId, product));
    }
}
