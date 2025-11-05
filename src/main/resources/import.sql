INSERT INTO tb_user (name, email, phone, password, birth_date) 
VALUES ('Maria Brown', 'maria@gmail.com', '988888888', '123456', '2001-07-25');

INSERT INTO tb_user (name, email, phone, password, birth_date) 
VALUES ('Alex Green', 'alex@gmail.com', '977777777', 'adm123', '1987-12-13');


INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');


-- João é CLIENT e ADMIN
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 2);

-- Maria é CLIENT
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);


INSERT INTO suppliers (name, phone, email, address)
VALUES
    ('Fornecedor Alpha', '1122334455', 'alpha@fornecedores.com', 'Rua das Laranjeiras, 123'),
    ('Fornecedor Beta', '11988776655', 'beta@suprimentos.com', 'Av. Imperial, 500');


INSERT INTO products (name, price, img_url, create_date, last_update_date)
VALUES
    ('Bolo de Chocolate', 25.90, 'https://example.com/bolo.jpg', CURRENT_DATE, CURRENT_TIMESTAMP),
    ('Torta de Limão', 32.50, 'https://example.com/torta.jpg', CURRENT_DATE, CURRENT_TIMESTAMP);


INSERT INTO recipes (product_id, last_update_date, description, amount, client_id)
VALUES
    (1, CURRENT_TIMESTAMP, 'Receita de bolo de chocolate simples', 8, 1),
    (2, CURRENT_TIMESTAMP, 'Receita de torta de limão gelada', 6, 1);


INSERT INTO ingredients (
    name, brand, price_cost, img_url, create_date, last_update_date, 
    quantity_per_unit, unit, supplier_id, client_id
)
VALUES
    ('Farinha de Trigo', 'Dona Benta', 4.50, NULL, CURRENT_DATE, CURRENT_TIMESTAMP, 1, 'KILOGRAM', 1, 1),
    ('Açúcar Refinado', 'União', 3.20, NULL, CURRENT_DATE, CURRENT_TIMESTAMP, 1, 'KILOGRAM', 1, 1),
    ('Ovos', 'Granja Sol', 0.70, NULL, CURRENT_DATE, CURRENT_TIMESTAMP, 12, 'UNIT', 2, 1),
    ('Limão Tahiti', 'Natural', 0.50, NULL, CURRENT_DATE, CURRENT_TIMESTAMP, 1, 'UNIT', 2, 1);


INSERT INTO recipe_items (
    recipe_id, ingredient_id, sub_product_id, quantity, unit_cost, total_cost
)
VALUES
    (1, 1, NULL, 0.5, 2.25, 2.25),
    (1, 2, NULL, 0.3, 0.96, 0.96),
    (1, 3, NULL, 4, 0.70, 2.80);


INSERT INTO recipe_items (
    recipe_id, ingredient_id, sub_product_id, quantity, unit_cost, total_cost
)
VALUES
    (2, 2, NULL, 0.2, 0.64, 0.64),
    (2, 3, NULL, 3, 0.70, 2.10),
    (2, 4, NULL, 4, 0.50, 2.00);
