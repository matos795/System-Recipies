-- ==========================================================
-- 🧹 LIMPEZA (remover tabelas antigas)
-- ATENÇÃO: ordem invertida para evitar conflitos
-- ==========================================================
DROP TABLE IF EXISTS recipe_items CASCADE;
DROP TABLE IF EXISTS recipes CASCADE;
DROP TABLE IF EXISTS ingredients CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS tb_user_role CASCADE;
DROP TABLE IF EXISTS tb_user CASCADE;
DROP TABLE IF EXISTS tb_role CASCADE;

-- ==========================================================
-- 👤 TABELA tb_role
-- ==========================================================
CREATE TABLE tb_role (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    authority VARCHAR(255) NOT NULL
);

-- ==========================================================
-- 👤 TABELA tb_user
-- ==========================================================
CREATE TABLE tb_user (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    birth_date DATE,
    password VARCHAR(255) NOT NULL
);

-- ==========================================================
-- 🔗 TABELA tb_user_role (Many-To-Many)
-- ==========================================================
CREATE TABLE tb_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_userrole_user
        FOREIGN KEY (user_id) REFERENCES tb_user(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_userrole_role
        FOREIGN KEY (role_id) REFERENCES tb_role(id)
        ON DELETE CASCADE
);

-- ==========================================================
-- 🏭 TABELA SUPPLIERS
-- ==========================================================
CREATE TABLE suppliers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(100),
    email VARCHAR(100),
    address VARCHAR(100),
    client_id BIGINT,
    CONSTRAINT fk_supplier_user FOREIGN KEY (client_id) REFERENCES tb_user(id)
    ON DELETE SET NULL
);

-- ==========================================================
-- 🧱 TABELA PRODUCTS
-- ==========================================================
CREATE TABLE products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    img_url VARCHAR(255),
    create_date DATE NOT NULL,
    last_update_date TIMESTAMP
);

-- ==========================================================
-- 📘 TABELA RECIPES (usa @MapsId -> product_id é PK e FK)
-- agora com client_id
-- ==========================================================
CREATE TABLE recipes (
    product_id BIGINT PRIMARY KEY,
    last_update_date TIMESTAMP,
    description VARCHAR(255),
    amount INT,
    client_id BIGINT,
    
    CONSTRAINT fk_recipe_product FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_recipe_user FOREIGN KEY (client_id) REFERENCES tb_user(id)
);

-- ==========================================================
-- 🧂 TABELA INGREDIENTS
-- adicionada FK client_id
-- ==========================================================
CREATE TABLE ingredients (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(50),
    price_cost NUMERIC(10,2) NOT NULL,
    img_url VARCHAR(255),
    create_date DATE NOT NULL,
    last_update_date TIMESTAMP,
    quantity_per_unit NUMERIC(10,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    supplier_id BIGINT,
    client_id BIGINT,

    CONSTRAINT fk_ingredient_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_ingredient_user FOREIGN KEY (client_id) REFERENCES tb_user(id)
);

-- ==========================================================
-- 🍰 TABELA RECIPE_ITEMS
-- ==========================================================
CREATE TABLE recipe_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    sub_product_id BIGINT,
    ingredient_id BIGINT,
    quantity NUMERIC(10,2) NOT NULL,
    unit_cost NUMERIC(10,2) NOT NULL,
    total_cost NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_recipe_items_recipe FOREIGN KEY (recipe_id)
    REFERENCES recipes(product_id)
    ON DELETE CASCADE,
    CONSTRAINT fk_recipe_items_product FOREIGN KEY (sub_product_id) REFERENCES products(id),
    CONSTRAINT fk_recipe_items_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),

    CONSTRAINT chk_one_fk CHECK (
        (sub_product_id IS NOT NULL AND ingredient_id IS NULL)
        OR (sub_product_id IS NULL AND ingredient_id IS NOT NULL)
    )
);
