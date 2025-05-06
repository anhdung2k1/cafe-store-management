package com.example.authentication.service.implement;

import com.amazonaws.services.s3.AmazonS3;
import com.example.authentication.entity.ProductEntity;
import com.example.authentication.entity.RatingEntity;
import com.example.authentication.model.Product;
import com.example.authentication.repository.ProductRepository;
import com.example.authentication.repository.RatingRepository;
import com.example.authentication.service.interfaces.ProductService;
import com.example.authentication.utils.S3Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final S3Utils s3Utils;

    @Value("${bucket.name}")
    public String bucketName;

    private final AmazonS3 s3Client;

    private Map<String, Object> productMap(ProductEntity productEntity) {
        return new HashMap<>() {{
            put("productID", productEntity.getProductID());
            put("productName", productEntity.getProductName());
            put("productModel", productEntity.getProductModel());
            put("productType", productEntity.getProductType());
            put("productQuantity", productEntity.getProductQuantity());
            put("productPrice", productEntity.getProductPrice());
            put("productDescription", productEntity.getProductDescription());
            put("imageUrl", Optional.ofNullable(productEntity.getImageUrl()).orElse(""));
            put("rating", productEntity.getRate());
        }};
    }

    @Override
    public Boolean createProduct(Product product) throws Exception {
        try {
            ProductEntity productEntity = new ProductEntity();
            product.setCreateAt(LocalDateTime.now());
            product.setUpdateAt(LocalDateTime.now());

            RatingEntity ratingEntity = new RatingEntity();
            ratingRepository.save(ratingEntity);
            product.setRate(ratingEntity);

            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                URL objectURL = s3Utils.getS3URL(product.getImageUrl());
                product.setImageUrl(objectURL.toString());
            }

            BeanUtils.copyProperties(product, productEntity);
            productRepository.save(productEntity);
            return true;
        } catch (Exception e) {
            throw new Exception("Could not create product: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllProducts() throws Exception {
        try {
            List<ProductEntity> productEntities = productRepository.findAllProducts();
            List<Map<String, Object>> result = new ArrayList<>();
            productEntities.forEach(entity -> result.add(productMap(entity)));
            return result;
        } catch (Exception e) {
            throw new Exception("Could not retrieve all products", e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllProductsByName(String productName) throws Exception {
        try {
            List<ProductEntity> productEntities = productRepository.findAllByProductNameContains(productName);
            List<Map<String, Object>> result = new ArrayList<>();
            productEntities.forEach(entity -> result.add(productMap(entity)));
            return result;
        } catch (Exception e) {
            throw new Exception("Could not retrieve products by name: " + productName, e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllProductsByType(String productType) throws Exception {
        try {
            List<ProductEntity> productEntities = productRepository.findAllByProductTypeContains(productType);
            List<Map<String, Object>> result = new ArrayList<>();
            productEntities.forEach(entity -> result.add(productMap(entity)));
            return result;
        } catch (Exception e) {
            throw new Exception("Could not retrieve products by type: " + productType, e);
        }
    }

    @Override
    public List<String> getProductCategories() throws Exception {
        try {
            return productRepository.findProductCategories();
        } catch (Exception e) {
            throw new Exception("Could not retrieve product categories", e);
        }
    }

    @Override
    public Map<String, Object> getProductById(Long productId) throws Exception {
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with ID: " + productId));
        return productMap(entity);
    }

    @Override
    public Boolean updateProductInformation(Long productId, Product product) throws Exception {
        try {
            ProductEntity productEntity = productRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException("Product not found with ID: " + productId));

            productEntity.setProductName(product.getProductName());
            productEntity.setProductModel(product.getProductModel());
            productEntity.setProductType(product.getProductType());
            productEntity.setProductQuantity(product.getProductQuantity());
            productEntity.setProductPrice(product.getProductPrice());
            productEntity.setProductDescription(product.getProductDescription());
            productEntity.setUpdateAt(LocalDateTime.now());

            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                URL objectURL = s3Utils.getS3URL(product.getImageUrl());
                productEntity.setImageUrl(objectURL.toString());
            }

            productRepository.save(productEntity);
            BeanUtils.copyProperties(productEntity, product);
            return true;
        } catch (Exception e) {
            throw new Exception("Could not update product: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean deleteProduct(Long productId) throws Exception {
        try {
            ProductEntity entity = productRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException("Product not found with ID: " + productId));

            String imageUrl = entity.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                s3Client.deleteObject(bucketName, fileName);
                log.info("Deleted image from S3: {}", fileName);
            }

            productRepository.delete(entity);
            return true;
        } catch (Exception e) {
            throw new Exception("Could not delete product: " + e.getMessage(), e);
        }
    }
}