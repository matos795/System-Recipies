-- ==========================================================
-- 🧹 LIMPEZA (remover tabelas antigas se existirem)
-- ==========================================================
DROP TABLE IF EXISTS recipe_items CASCADE;
DROP TABLE IF EXISTS recipes CASCADE;
DROP TABLE IF EXISTS ingredients CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS products CASCADE;

-- ==========================================================
-- 🧱 TABELA PRODUCTS
-- ==========================================================
CREATE TABLE products (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    img_url VARCHAR(255),
    create_date DATE NOT NULL,
    last_update_date TIMESTAMP
);

-- ==========================================================
-- 📘 TABELA RECIPES (usa @MapsId -> product_id é PK e FK)
-- ==========================================================
CREATE TABLE recipes (
    product_id INT PRIMARY KEY,
    last_update_date TIMESTAMP,
    description VARCHAR(255),
    amount INT,
    CONSTRAINT fk_recipe_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ==========================================================
-- 🏭 TABELA SUPPLIERS
-- ==========================================================
CREATE TABLE suppliers (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(100),
    email VARCHAR(100),
    address VARCHAR(100)
);

-- ==========================================================
-- 🧂 TABELA INGREDIENTS
-- ==========================================================
CREATE TABLE ingredients (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(50),
    price_cost NUMERIC(10,2) NOT NULL,
    img_url VARCHAR(255),
    create_date DATE NOT NULL,
    last_update_date TIMESTAMP,
    quantity_per_unit NUMERIC(10,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    supplier_id INT,
    CONSTRAINT fk_ingredient_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- ==========================================================
-- 🍰 TABELA RECIPE_ITEMS
-- ==========================================================
CREATE TABLE recipe_items (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipe_id INT NOT NULL,
    sub_product_id INT,
    ingredient_id INT,
    quantity NUMERIC(10,2) NOT NULL,
    unit_cost NUMERIC(10,2) NOT NULL,
    total_cost NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_recipe_items_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(product_id),
    CONSTRAINT fk_recipe_items_product FOREIGN KEY (sub_product_id) REFERENCES products(id),
    CONSTRAINT fk_recipe_items_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),

    CONSTRAINT chk_one_fk CHECK (
        (sub_product_id IS NOT NULL AND ingredient_id IS NULL)
        OR (sub_product_id IS NULL AND ingredient_id IS NOT NULL)
    )
);
