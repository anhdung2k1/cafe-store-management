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
            UserEntity userEntity = userRepository.findById(userId).get();
            CartEntity cartEntity;
            if (cartRepository.getCartEntityWithUserId(userId).isPresent()) {
                // If the user already have the cart, use the current
                cartEntity = cartRepository.getCartEntityWithUserId(userId).get();
            } else {
                // If the user did not have create the cart create one in DB
                cartEntity = new CartEntity(userEntity);
            }
            if (product != null) {
                List<ProductEntity> cartProducts = cartEntity.getProducts();
                if (cartProducts == null) {
                    cartProducts = new ArrayList<>();
                }
                if (productRepository.findById(product.getProductID()).isPresent()) {
                    ProductEntity productAddEntity = productRepository.findById(product.getProductID()).get();

                    if (cartProducts != null) {
                        // Update the quantity if the item has been created
                        int index = cartProducts.indexOf(productAddEntity);
                        if (index != -1) {
                            productAddEntity.setProductQuantity(productAddEntity.getProductQuantity() + 1);
                            cartProducts.set(index, productAddEntity);
                        }
                        else {
                            log.warn("The product did not exists");
                            productAddEntity.setProductQuantity(1);
                            cartProducts.add(productAddEntity);
                        }
                    }
                    // Update the carts
                    cartEntity.setProducts(cartProducts);
                    cartRepository.save(cartEntity);
                }
                else {
                    log.warn("The product ID given is not present: " + product.getProductID());
                }
            }
            return cartMap(cartEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get cart items " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> updateCartItems(Long userId, Product product) throws Exception {
        try {
            CartEntity cartEntity = cartRepository.getCartEntityWithUserId(userId).isPresent()
                    ? cartRepository.getCartEntityWithUserId(userId).get() : null;
            assert cartEntity != null;
            if (product != null) {
                List<ProductEntity> cartProducts = cartEntity.getProducts();
                if (productRepository.findById(product.getProductID()).isPresent()) {
                    ProductEntity productRemoveEntity = productRepository.findById(product.getProductID()).get();
                    // Remove the product in the list
                    cartProducts.remove(productRemoveEntity);
                    // Update the carts again
                    cartEntity.setProducts(cartProducts);
                    cartRepository.save(cartEntity);
                }
                else {
                    log.warn("The product ID given is not present: " + product.getProductID());
                }
            }
            else {
                log.info("There is no items to remove");
            }
            return cartMap(cartEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get cart items " + e.getMessage());
        }
    }
}
