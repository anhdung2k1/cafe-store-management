package com.example.authentication.service.implement;

import com.example.authentication.entity.CartEntity;
import com.example.authentication.entity.ProductEntity;
import com.example.authentication.entity.UserEntity;
import com.example.authentication.model.Product;
import com.example.authentication.repository.CartRepository;
import com.example.authentication.repository.ProductRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.interfaces.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private Map<String, Object> cartMap(CartEntity cartEntity) {
        return new HashMap<>() {{
            put("cartID", cartEntity.getCartId());
            put("products", cartEntity.getProducts());
            put("userId", cartEntity.getUser().getUser_id());
            put("date", cartEntity.getCartDate());
        }};
    }

    @Override
    public Map<String, Object> getCartItems(Long userId) throws Exception {
        try {
            CartEntity cartEntity = cartRepository.getCartEntityWithUserId(userId).isPresent()
                    ? cartRepository.getCartEntityWithUserId(userId).get() : null;
            if (userRepository.findById(userId).isPresent()) {
                cartEntity.setUser(userRepository.findById(userId).get());
            }
            assert cartEntity != null;
            return cartMap(cartEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get cart items" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> addCartItems(Long userId, Product product) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
            CartEntity cartEntity = cartRepository.getCartEntityWithUserId(userId)
                    .orElse(new CartEntity(userEntity));

            if (product != null && product.getProductID() != null) {
                List<ProductEntity> cartProducts = cartEntity.getProducts() != null
                        ? cartEntity.getProducts() : new ArrayList<>();

                ProductEntity productAddEntity = productRepository.findById(product.getProductID())
                        .orElseThrow(() -> new Exception("Product not found"));

                int index = cartProducts.indexOf(productAddEntity);

                if (index != -1) {
                    // If product exists in cart → update quantity by adding new
                    ProductEntity existing = cartProducts.get(index);
                    int updatedQty = existing.getProductQuantity() + product.getProductQuantity();
                    existing.setProductQuantity(updatedQty);
                    cartProducts.set(index, existing);
                } else {
                    // New product → set quantity from client
                    productAddEntity.setProductQuantity(product.getProductQuantity());
                    cartProducts.add(productAddEntity);
                }

                cartEntity.setProducts(cartProducts);
                cartRepository.save(cartEntity);
            }

            return cartMap(cartEntity);
        } catch (Exception e) {
            throw new Exception("Could not add product to cart: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> updateCartItems(Long userId, Product product) throws Exception {
        try {
            CartEntity cartEntity = cartRepository.getCartEntityWithUserId(userId)
                    .orElseThrow(() -> new Exception("Cart not found"));
            
            List<ProductEntity> cartProducts = cartEntity.getProducts();

            Optional<ProductEntity> optionalProduct = productRepository.findById(product.getProductID());

            if (optionalProduct.isPresent()) {
                ProductEntity productEntity = optionalProduct.get();

                int index = cartProducts.indexOf(productEntity);
                if (index != -1) {
                    ProductEntity existing = cartProducts.get(index);
                    int currentQty = existing.getProductQuantity();
                    int newQty = product.getProductQuantity();

                    if (newQty < currentQty) {
                        log.info("Quantity reduced from {} to {}", currentQty, newQty);
                    } else {
                        log.info("Quantity increased from {} to {}", currentQty, newQty);
                    }

                    if (newQty <= 0) {
                        cartProducts.remove(index);
                    } else {
                        existing.setProductQuantity(newQty);
                        cartProducts.set(index, existing);
                    }
                }
                // update cart
                cartEntity.setProducts(cartProducts);
                cartRepository.save(cartEntity);
            } else {
                log.info("Product ID does not exist: {}", product.getProductID());
            }

            return cartMap(cartEntity);
        } catch (Exception e) {
            throw new Exception("Could not update cart items: " + e.getMessage(), e);
        }
    }
}
