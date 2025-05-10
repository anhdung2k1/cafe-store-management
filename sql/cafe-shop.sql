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

INSERT INTO users(user_name, address, birth_day, gender, image_url, create_at, update_at)
VALUES 
('barista01', 'District 1, HCM', '1990-05-12', 'Male', 'https://example.com/avatar1.jpg', NOW(), NOW()),
('barista02', 'District 2, HCM', '1992-07-20', 'Female', 'https://example.com/avatar2.jpg', NOW(), NOW()),
('customer01', 'District 3, HCM', '1995-01-01', 'Male', 'https://example.com/avatar3.jpg', NOW(), NOW()),
('customer02', 'District 4, HCM', '1989-03-14', 'Female', 'https://example.com/avatar4.jpg', NOW(), NOW()),
('staff01', 'District 5, HCM', '1998-08-30', 'Male', 'https://example.com/avatar5.jpg', NOW(), NOW()),
('staff02', 'District 6, HCM', '1991-04-18', 'Female', 'https://example.com/avatar6.jpg', NOW(), NOW()),
('guest01', 'District 7, HCM', '2000-12-12', 'Male', 'https://example.com/avatar7.jpg', NOW(), NOW()),
('guest02', 'District 8, HCM', '1994-10-05', 'Female', 'https://example.com/avatar8.jpg', NOW(), NOW()),
('manager01', 'District 9, HCM', '1985-02-27', 'Male', 'https://example.com/avatar9.jpg', NOW(), NOW()),
('admin01', 'District 10, HCM', '1980-06-06', 'Male', 'https://example.com/avatar10.jpg', NOW(), NOW());

INSERT INTO roles(role_name, role_description, create_at, update_at)
VALUES 
('ADMIN', 'Full access to system', NOW(), NOW()),
('USER', 'Standard user role', NOW(), NOW());


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

INSERT INTO roles_permissions(role_id, per_id)
VALUES 
(1,1),(1,2),(1,3),(1,4),
(1,5),(1,6),(1,7),(1,8),
(1,9),(1,10);

INSERT INTO carts(user_id, cart_date)
VALUES 
(1,NOW()), (2,NOW()), (3,NOW()), (4,NOW()), (5,NOW()),
(6,NOW()), (7,NOW()), (8,NOW()), (9,NOW()), (10,NOW());

INSERT INTO wishlist(user_id)
VALUES 
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10);

INSERT INTO rating(rating_count, rating_rate)
VALUES 
(100, 4.5),(90, 4.2),(85, 4.8),(70, 4.6),(95, 4.7),
(110, 4.3),(130, 4.9),(60, 4.1),(75, 4.4),(80, 4.6);

INSERT INTO product(product_name, product_model, product_type, product_quant, product_price, product_desc, image_url, rating_id, create_at, update_at)
VALUES 
('Espresso', 'Cup', 'Coffee', 100, 2.50, 'Strong black coffee', 'https://example.com/espresso.jpg', 1, NOW(), NOW()),
('Latte', 'Tall', 'Coffee', 120, 3.75, 'Espresso with milk', 'https://example.com/latte.jpg', 2, NOW(), NOW()),
('Cappuccino', 'Medium', 'Coffee', 90, 3.50, 'Foamy milk & espresso', 'https://example.com/cappuccino.jpg', 3, NOW(), NOW()),
('Mocha', 'Large', 'Coffee', 80, 4.00, 'Chocolate + espresso', 'https://example.com/mocha.jpg', 4, NOW(), NOW()),
('Americano', 'Regular', 'Coffee', 110, 2.80, 'Watered espresso', 'https://example.com/americano.jpg', 5, NOW(), NOW()),
('Cold Brew', 'Bottle', 'Coffee', 150, 3.20, 'Brewed cold', 'https://example.com/coldbrew.jpg', 6, NOW(), NOW()),
('Flat White', 'Tall', 'Coffee', 75, 3.90, 'Smooth texture', 'https://example.com/flatwhite.jpg', 7, NOW(), NOW()),
('Macchiato', 'Small', 'Coffee', 60, 3.30, 'Espresso w/ foam', 'https://example.com/macchiato.jpg', 8, NOW(), NOW()),
('Irish Coffee', 'Glass', 'Coffee', 40, 5.00, 'Whiskey + coffee', 'https://example.com/irish.jpg', 9, NOW(), NOW()),
('Affogato', 'Dessert', 'Coffee', 50, 4.25, 'Ice cream + espresso', 'https://example.com/affogato.jpg', 10, NOW(), NOW());

INSERT INTO payment(pay_method, pay_desc, image_url, create_at, update_at) VALUES 
('Credit Card', 'Pay with Visa or MasterCard', 'https://example.com/payment1.png', NOW(), NOW()),
('Cash', 'Pay with physical currency', 'https://example.com/payment2.png', NOW(), NOW()),
('MOMO', 'Pay with MOMO wallet', 'https://example.com/payment3.png', NOW(), NOW()),
('PayPal', 'Online PayPal payment', 'https://example.com/payment5.png', NOW(), NOW()),
('Bank Transfer', 'Transfer to bank account', 'https://example.com/payment6.png', NOW(), NOW());


INSERT INTO transactions(trans_type, shipping_address, billing_payment, pay_id, create_at, update_at) VALUES 
('Purchase', '12 Pasteur St, District 1', 45.50, 1, NOW(), NOW()),
('Refund', '34 Tran Hung Dao, District 5', 30.00, 2, NOW(), NOW()),
('Sale', '7 Le Loi, District 3', 72.25, 3, NOW(), NOW()),
('Purchase', '99 Nguyen Hue, District 1', 110.75, 4, NOW(), NOW()),
('Sale', '1 Dinh Tien Hoang, District 1', 65.00, 5, NOW(), NOW()),
('Refund', '88 Hai Ba Trung, District 3', 28.50, 6, NOW(), NOW()),
('Purchase', '56 Cach Mang Thang 8, District 10', 95.00, 7, NOW(), NOW()),
('Sale', '42 Nguyen Thi Minh Khai, District 1', 120.00, 8, NOW(), NOW()),
('Purchase', '25 Ly Tu Trong, District 1', 70.00, 9, NOW(), NOW()),
('Refund', '33 Nguyen Van Cu, District 5', 40.00, 10, NOW(), NOW());


INSERT INTO user_payment(pay_id, user_id, trans_id) VALUES 
(1, 1, 1),
(2, 2, 2),
(3, 3, 3),
(4, 4, 4),
(5, 5, 5),
(6, 6, 6),
(7, 7, 7),
(8, 8, 8),
(9, 9, 9),
(10, 10, 10);

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
