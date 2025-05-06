package com.example.authentication.service.interfaces;

import com.example.authentication.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    Boolean createProduct(Product product) throws Exception;
    List<Map<String, Object>> getAllProducts() throws Exception;
    List<Map<String, Object>> getAllProductsByName(String productName) throws Exception;
    List<Map<String, Object>> getAllProductsByType(String productType) throws Exception;
    List<String> getProductCategories() throws Exception;
    Map<String, Object> getProductById(Long productId) throws Exception;
    Boolean updateProductInformation(Long productId, Product product) throws Exception;
    Boolean deleteProduct(Long productId) throws Exception;
}