-- ==========================================================
-- 👤 USERS COM SENHA BCRYPT
-- ==========================================================
INSERT INTO tb_user (name, email, phone, password, birth_date) VALUES ('Maria Brown', 'maria@gmail.com', '988888888', '$2a$10$j2MTOveFRm86IONZfqigVuwh4WvHrF64783Rut4EQioqNd2YHTYei', '2001-07-25');
INSERT INTO tb_user (name, email, phone, password, birth_date) VALUES ('Alex Green', 'alex@gmail.com', '977777777', '$2a$10$j2MTOveFRm86IONZfqigVuwh4WvHrF64783Rut4EQioqNd2YHTYei', '1987-12-13');


-- ==========================================================
-- 👤 ROLES
-- ==========================================================
INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');


-- ==========================================================
-- 🔗 USER ROLES
-- ==========================================================
-- Maria (id=1) é CLIENT (1) e ADMIN (2)
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 2);

-- Alex (id=2) é CLIENT
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);


-- ==========================================================
-- 🏭 SUPPLIERS (agora precisam de client_id)
-- ==========================================================
INSERT INTO suppliers (name, phone, email, address, client_id) VALUES ('Fornecedor Alpha', '1122334455', 'alpha@fornecedores.com', 'Rua das Laranjeiras, 123', 1);
INSERT INTO suppliers (name, phone, email, address, client_id) VALUES ('Fornecedor Beta', '11988776655', 'beta@suprimentos.com', 'Av. Imperial, 500', 1);


-- ==========================================================
-- 🧱 PRODUCTS
-- ==========================================================
INSERT INTO products (name, price, img_url, create_date, last_update_date) VALUES ('Bolo de Chocolate', 25.90, 'https://example.com/bolo.jpg', CURRENT_DATE, CURRENT_TIMESTAMP);
INSERT INTO products (name, price, img_url, create_date, last_update_date) VALUES ('Torta de Limão', 32.50, 'https://example.com/torta.jpg', CURRENT_DATE, CURRENT_TIMESTAMP);


-- ==========================================================
-- 📘 RECIPES (product_id == id do product)
-- ==========================================================
INSERT INTO recipes (product_id, last_update_date, description, amount, client_id) VALUES (1, CURRENT_TIMESTAMP, 'Receita de bolo de chocolate simples', 8, 1);
INSERT INTO recipes (product_id, last_update_date, description, amount, client_id) VALUES (2, CURRENT_TIMESTAMP, 'Receita de torta de limão gelada', 6, 1);


-- ==========================================================
-- 🧂 INGREDIENTS
-- ==========================================================
INSERT INTO ingredients (name, brand, price_cost, img_url, create_date, last_update_date, quantity_per_unit, unit, supplier_id, client_id) VALUES ('Farinha de Trigo', 'Dona Benta', 4.50, NULL, CURRENT_DATE, CURRENT_TIMESTAMP, 1, 'KILOGRAM', 1, 1);
INSERT INTO ingredients (name, brand, price_cost, img_url, create_date, last_update_date, quantity_per_unit, unit, supplier_id, client_id) VALUES ('Açúcar Refinado', 'União', 3.20, NULL, CURRENT_DATE, CURRENT_TIMESTAMP, 1, 'KILOGRAM', 1, 1);
INSERT INTO ingredients (name, brand, price_cost, img_url, create_date, last_update_date, quantity_per_unit, unit, supplier_id, client_id) VALUES ('Ovos', 'Granja Sol', 0.70, NULL, CURRENT_DATE, CURRENT_TIMESTAMP, 12, 'UNIT', 2, 1);
INSERT INTO ingredients (name, brand, price_cost, img_url, create_date, last_update_date, quantity_per_unit, unit, supplier_id, client_id) VALUES ('Limão Tahiti', 'Natural', 0.50, NULL, CURRENT_DATE, CURRENT_TIMESTAMP, 1, 'UNIT', 2, 1);

