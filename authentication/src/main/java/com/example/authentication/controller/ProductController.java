package com.example.authentication.controller;

import com.example.authentication.model.Product;
import com.example.authentication.service.interfaces.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Create new Product
    @PostMapping(value = "/products")
    public ResponseEntity<Boolean> createProduct(@RequestBody Product product) throws Exception {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    // Get all Products
    @GetMapping(value = "/products")
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() throws Exception {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Get all Products by name
    @GetMapping(value = "/products/query")
    public ResponseEntity<List<Map<String, Object>>> getAllProductsByName(@RequestParam("query") String productName) throws Exception {
        return ResponseEntity.ok(productService.getAllProductsByName(productName));
    }

    // Get all Products by type
    @GetMapping(value = "/products/types/query")
    public ResponseEntity<List<Map<String, Object>>> getAllProductsByType(@RequestParam("query") String productType) throws Exception {
        return ResponseEntity.ok(productService.getAllProductsByType(productType));
    }

    // Get all product categories
    @GetMapping(value = "/products/categories")
    public ResponseEntity<List<String>> getProductCategories() throws Exception {
        return ResponseEntity.ok(productService.getProductCategories());
    }

    // Get Product by ID
    @GetMapping(value = "/products/{productId}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable("productId") Long productId) throws Exception {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    // Update Product
    @PatchMapping(value = "/products/{productId}")
    public ResponseEntity<Boolean> updateProduct(@PathVariable("productId") Long productId, @RequestBody Product product) throws Exception {
        return ResponseEntity.ok(productService.updateProductInformation(productId, product));
    }

    // Delete Product
    @DeleteMapping(value = "/products/{productId}")
    public ResponseEntity<Map<String, Boolean>> deleteProduct(@PathVariable("productId") Long productId) throws Exception {
        return ResponseEntity.ok(new HashMap<>() {{
            put("deleted", productService.deleteProduct(productId));
        }});
    }
}