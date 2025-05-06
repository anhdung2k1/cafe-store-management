package com.example.authentication.service.implement;

import com.example.authentication.entity.ProductEntity;
import com.example.authentication.entity.UserEntity;
import com.example.authentication.entity.WishListEntity;
import com.example.authentication.model.Product;
import com.example.authentication.repository.ProductRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.repository.WishListRepository;
import com.example.authentication.service.interfaces.WishListService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private Map<String, Object> wishListMap(WishListEntity wishListEntity) {
        return new HashMap<>() {{
            put("wishListID", wishListEntity.getWishListId());
            put("products", wishListEntity.getProducts());
            put("userId", wishListEntity.getUser().getUser_id());
        }};
    }

    @Override
    public Map<String, Object> getWishListItems(Long userId) throws Exception {
        try {
            WishListEntity wishListEntity = wishListRepository.getWishListEntityWithUserId(userId).isPresent()
                    ? wishListRepository.getWishListEntityWithUserId(userId).get() : null;
            if (userRepository.findById(userId).isPresent()) {
                wishListEntity.setUser(userRepository.findById(userId).get());
            }
            assert wishListEntity != null;
            return wishListMap(wishListEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get wish list items" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> addWishListItems(Long userId, Product product) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(userId).get();
            WishListEntity wishListEntity;
            if (wishListRepository.getWishListEntityWithUserId(userId).isPresent()) {
                // If the user already have the cart, use the current
                wishListEntity = wishListRepository.getWishListEntityWithUserId(userId).get();
            } else {
                // If the user did not have create the cart create one in DB
                wishListEntity = new WishListEntity(userEntity);
            }
            if (product != null) {
                List<ProductEntity> wishListProducts = wishListEntity.getProducts();
                if (wishListProducts == null) {
                    wishListProducts = new ArrayList<>();
                }
                if (productRepository.findById(product.getProductID()).isPresent()) {
                    ProductEntity productAddEntity = productRepository.findById(product.getProductID()).get();

                    if (wishListProducts != null) {
                        // Update the quantity if the item has been created
                        int index = wishListProducts.indexOf(productAddEntity);
                        if (index != -1) {
                            log.error("Current item already appended in the wishlist items");
                        }
                        else {
                            log.info("The product did not exists, add to wishlist item");
                            wishListProducts.add(productAddEntity);
                        }
                    }
                    // Update the carts
                    wishListEntity.setProducts(wishListProducts);
                    wishListRepository.save(wishListEntity);
                }
                else {
                    log.warn("The product ID given is not present: " + product.getProductID());
                }
            }
            return wishListMap(wishListEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get cart items " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> removeWishListItems(Long userId, Product product) throws Exception {
        try {
            WishListEntity wishListEntity = wishListRepository.getWishListEntityWithUserId(userId).isPresent()
                    ? wishListRepository.getWishListEntityWithUserId(userId).get() : null;
            assert wishListEntity != null;
            if (product != null) {
                List<ProductEntity> wishListproducts = wishListEntity.getProducts();
                if (productRepository.findById(product.getProductID()).isPresent()) {
                    ProductEntity productRemoveEntity = productRepository.findById(product.getProductID()).get();
                    // Remove the product in the list
                    wishListproducts.remove(productRemoveEntity);
                    // Update the carts again
                    wishListEntity.setProducts(wishListproducts);
                    wishListRepository.save(wishListEntity);
                }
                else {
                    log.warn("The product ID given is not present: " + product.getProductID());
                }
            }
            else {
                log.info("There is no items to remove");
            }
            return wishListMap(wishListEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get wish list items " + e.getMessage());
        }
    }

    @Override
    public Boolean removeAllWishListItems(Long userId) throws Exception {
        try {
            WishListEntity wishListEntity = wishListRepository.getWishListEntityWithUserId(userId).isPresent()
                    ? wishListRepository.getWishListEntityWithUserId(userId).get() : null;
            assert wishListEntity != null;
            List<ProductEntity> wishListproducts = wishListEntity.getProducts();
            wishListproducts.clear();
            wishListEntity.setProducts(wishListproducts);
            wishListRepository.save(wishListEntity);
            return true;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get wish list items" + e.getMessage());
        }
    }
}
