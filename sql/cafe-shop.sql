-- USERS TABLE
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255),
    birth_day VARCHAR(255),
    gender VARCHAR(50),
    image_url VARCHAR(255),
    create_at DATETIME(6),
    update_at DATETIME(6)
);

-- ROLES TABLE
CREATE TABLE roles (
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(255) NOT NULL,
    role_description VARCHAR(255),
    create_at DATETIME(6),
    update_at DATETIME(6)
);

-- PERMISSIONS
CREATE TABLE permission (
    per_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    per_module VARCHAR(255),
    per_name VARCHAR(255),
    create_at DATETIME(6),
    update_at DATETIME(6)
);

-- ROLES-PERMISSIONS JOIN
CREATE TABLE roles_permissions (
    role_id BIGINT,
    per_id BIGINT,
    PRIMARY KEY (role_id, per_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (per_id) REFERENCES permission(per_id) ON DELETE CASCADE
);

-- ACCOUNTS
CREATE TABLE accounts (
    acc_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT,
    user_id BIGINT,
    user_name VARCHAR(255) NOT NULL,
    hash_pass VARCHAR(255) NOT NULL,
    create_at DATETIME(6),
    update_at DATETIME(6),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- CARTS
CREATE TABLE carts (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    cart_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- WISHLIST
CREATE TABLE wishlist (
    wishlist_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- RATING
CREATE TABLE rating (
    rating_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rating_count INT NOT NULL,
    rating_rate DOUBLE(10, 2) NOT NULL
);

-- PRODUCT
CREATE TABLE product (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(255) NOT NULL,
    product_model VARCHAR(255),
    product_type VARCHAR(255),
    product_quant INT NOT NULL,
    product_price DOUBLE(10, 2) NOT NULL,
    product_desc VARCHAR(255),
    image_url VARCHAR(255),
    rating_id BIGINT,
    wishlist_id BIGINT,
    cart_id BIGINT,
    create_at DATETIME(6),
    update_at DATETIME(6),
    FOREIGN KEY (rating_id) REFERENCES rating(rating_id),
    FOREIGN KEY (wishlist_id) REFERENCES wishlist(wishlist_id),
    FOREIGN KEY (cart_id) REFERENCES carts(cart_id)
);

-- PAYMENT
CREATE TABLE payment (
    pay_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pay_method VARCHAR(255) NOT NULL,
    pay_desc VARCHAR(255),
    image_url VARCHAR(255),
    create_at DATETIME(6),
    update_at DATETIME(6)
);

-- TRANSACTIONS
CREATE TABLE transactions (
    trans_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trans_type VARCHAR(255),
    shipping_address VARCHAR(255),
    billing_payment DOUBLE(10, 2),
    pay_id BIGINT,
    create_at DATETIME(6),
    update_at DATETIME(6),
    FOREIGN KEY (pay_id) REFERENCES payment(pay_id)
);

-- USER-PAYMENT
CREATE TABLE user_payment (
    pay_id BIGINT,
    user_id BIGINT,
    trans_id BIGINT,
    PRIMARY KEY (pay_id, user_id, trans_id),
    FOREIGN KEY (pay_id) REFERENCES payment(pay_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (trans_id) REFERENCES transactions(trans_id)
);

-- ORDERS
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    trans_id BIGINT,
    order_date VARCHAR(255),
    order_status VARCHAR(255),
    total_amount DOUBLE(10, 2),
    create_at DATETIME(6),
    update_at DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (trans_id) REFERENCES transactions(trans_id)
);

-- 1. ROLES
INSERT INTO roles(role_name, role_description, create_at, update_at)
VALUES 
('ADMIN', 'Full access to system', NOW(), NOW()),
('USER', 'Standard user role', NOW(), NOW());

-- 2. PERMISSIONS
INSERT INTO permission(per_module, per_name, create_at, update_at)
VALUES 
('PRODUCT', 'READ', NOW(), NOW()),
('PRODUCT', 'CREATE', NOW(), NOW()),
('PRODUCT', 'UPDATE', NOW(), NOW()),
('PRODUCT', 'DELETE', NOW(), NOW()),
('ORDER', 'READ', NOW(), NOW()),
('ORDER', 'CREATE', NOW(), NOW()),
('ORDER', 'UPDATE', NOW(), NOW()),
('ORDER', 'DELETE', NOW(), NOW()),
('USER', 'MANAGE', NOW(), NOW()),
('SYSTEM', 'ACCESS', NOW(), NOW());

-- 3. ROLES_PERMISSIONS
INSERT INTO roles_permissions(role_id, per_id)
VALUES 
(1,1),(1,2),(1,3),(1,4),
(1,5),(1,6),(1,7),(1,8),
(1,9),(1,10);

-- 4. USERS
INSERT INTO users(user_name, address, birth_day, gender, image_url, create_at, update_at)
VALUES 
('barista01', 'District 1, HCM', '1990-05-12', 'Male', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/barista_1.png', NOW(), NOW()),
('barista02', 'District 2, HCM', '1992-07-20', 'Female', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/barista_2.png', NOW(), NOW()),
('customer01', 'District 3, HCM', '1995-01-01', 'Male', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/customer_1.jpg', NOW(), NOW()),
('customer02', 'District 4, HCM', '1989-03-14', 'Female', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/customer_2.jpg', NOW(), NOW()),
('staff01', 'District 5, HCM', '1998-08-30', 'Male', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/staff_1.jpg', NOW(), NOW()),
('staff02', 'District 6, HCM', '1991-04-18', 'Female', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/staff_2.jpg', NOW(), NOW()),
('guest01', 'District 7, HCM', '2000-12-12', 'Male', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/guest_1.jpg', NOW(), NOW()),
('guest02', 'District 8, HCM', '1994-10-05', 'Female', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/guest_2.jpg', NOW(), NOW()),
('manager01', 'District 9, HCM', '1985-02-27', 'Male', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/manager_1.jpg', NOW(), NOW()),
('admin01', 'District 10, HCM', '1980-06-06', 'Male', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/admin_1.jpg', NOW(), NOW());

-- 5. ACCOUNTS
INSERT INTO accounts(role_id, user_id, user_name, hash_pass, create_at, update_at) VALUES
(1, 10, 'admin01', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW()),
(2, 1, 'barista01', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW()),
(2, 2, 'barista02', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW()),
(2, 3, 'customer01', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW()),
(2, 4, 'customer02', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW()),
(2, 5, 'staff01', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW()),
(2, 6, 'staff02', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW()),
(2, 7, 'guest01', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW()),
(2, 8, 'guest02', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW()),
(2, 9, 'manager01', '$2a$10$4GIKc/xjK8zNqsDvBsJY1OePPeium.J3NAhl64O3EQDQ3FKTfl5uq', NOW(), NOW());

-- 6. CARTS
INSERT INTO carts(user_id, cart_date)
VALUES 
(1,NOW()), (2,NOW()), (3,NOW()), (4,NOW()), (5,NOW()),
(6,NOW()), (7,NOW()), (8,NOW()), (9,NOW()), (10,NOW());

-- 7. WISHLIST
INSERT INTO wishlist(user_id)
VALUES 
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10);

-- 8. RATING
INSERT INTO rating(rating_count, rating_rate)
VALUES 
(100, 4.5),(90, 4.2),(85, 4.8),(70, 4.6),(95, 4.7),
(110, 4.3),(130, 4.9),(60, 4.1),(75, 4.4),(80, 4.6);

-- 9. PAYMENT
INSERT INTO payment(pay_method, pay_desc, image_url, create_at, update_at) VALUES 
('Credit Card', 'Pay with Visa or MasterCard', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Credit_card.png', NOW(), NOW()),
('Cash', 'Pay with physical currency', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Cash.png', NOW(), NOW()),
('PayPal', 'Online PayPal payment', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/paypal.png', NOW(), NOW()),
('Bank Transfer', 'Transfer to bank account', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Bank_Transfer.png', NOW(), NOW());

-- 10. TRANSACTIONS
INSERT INTO transactions(trans_type, shipping_address, billing_payment, pay_id, create_at, update_at) VALUES 
('Purchase', '12 Pasteur St', 45.50, 1, NOW(), NOW()),
('Refund', 'Tran Hung Dao St', 30.00, 2, NOW(), NOW()),
('Sale', 'Le Loi St', 72.25, 3, NOW(), NOW()),
('Purchase', 'Nguyen Hue St', 110.75, 4, NOW(), NOW()),
('Sale', 'Dinh Tien Hoang St', 65.00, 1, NOW(), NOW()),
('Refund', 'Hai Ba Trung St', 28.50, 2, NOW(), NOW()),
('Purchase', 'CMT8 St', 95.00, 3, NOW(), NOW()),
('Sale', 'Nguyen Thi Minh Khai St', 120.00, 4, NOW(), NOW()),
('Purchase', 'Ly Tu Trong St', 70.00, 1, NOW(), NOW()),
('Refund', 'Nguyen Van Cu St', 40.00, 2, NOW(), NOW());

-- 11. USER_PAYMENT
INSERT INTO user_payment(pay_id, user_id, trans_id) VALUES 
(1, 5, 5),
(2, 6, 6),
(3, 7, 7),
(4, 8, 8),
(1, 9, 9),
(2, 10, 10);

-- 12. ORDERS
INSERT INTO orders(user_id, trans_id, order_date, order_status, total_amount, create_at, update_at) VALUES 
(1, 1, '2025-05-10', 'COMPLETED', 45.50, NOW(), NOW()),
(2, 2, '2025-05-10', 'COMPLETED', 30.00, NOW(), NOW()),
(3, 3, '2025-05-10', 'COMPLETED', 72.25, NOW(), NOW()),
(4, 4, '2025-05-10', 'COMPLETED', 110.75, NOW(), NOW()),
(5, 5, '2025-05-10', 'COMPLETED', 65.00, NOW(), NOW()),
(6, 6, '2025-05-10', 'COMPLETED', 28.50, NOW(), NOW()),
(7, 7, '2025-05-10', 'COMPLETED', 95.00, NOW(), NOW()),
(8, 8, '2025-05-10', 'COMPLETED', 120.00, NOW(), NOW()),
(9, 9, '2025-05-10', 'COMPLETED', 70.00, NOW(), NOW()),
(10, 10, '2025-05-10', 'COMPLETED', 40.00, NOW(), NOW());

-- 13. PRODUCT
INSERT INTO product(product_name, product_model, product_type, product_quant, product_price, product_desc, image_url, rating_id, create_at, update_at)
VALUES 
('Espresso', 'Cup', 'Popular', 100, 2.50, 'Strong black coffee', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Espresso.jpg', 1, NOW(), NOW()),
('Latte', 'Tall', 'Offer', 120, 3.75, 'Espresso with milk', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Latte.jpg', 2, NOW(), NOW()),
('Cappuccino', 'Medium', 'Popular', 90, 3.50, 'Foamy milk & espresso', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Cappuccino.jpg', 3, NOW(), NOW()),
('Mocha', 'Large', 'Offer', 80, 4.00, 'Chocolate + espresso', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Mocha.jpg', 4, NOW(), NOW()),
('Americano', 'Regular', 'Offer', 110, 2.80, 'Watered espresso', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Americano.jpg', 5, NOW(), NOW()),
('Cold Brew', 'Bottle', 'Offer', 150, 3.20, 'Brewed cold', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Cold_Brew.jpg', 6, NOW(), NOW()),
('Flat White', 'Tall', 'Popular', 75, 3.90, 'Smooth texture', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Flat_White.jpg', 7, NOW(), NOW()),
('Macchiato', 'Small', 'Popular', 60, 3.30, 'Espresso w/ foam', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Macchiato.jpg', 8, NOW(), NOW()),
('Irish Coffee', 'Glass', 'Offer', 40, 5.00, 'Whiskey + coffee', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Irish_Coffee.jpg', 9, NOW(), NOW()),
('Affogato', 'Dessert', 'Offer', 50, 4.25, 'Ice cream + espresso', 'https://product-bucket-storage.s3.us-east-1.amazonaws.com/product_images/Affogato.jpg', 10, NOW(), NOW());
