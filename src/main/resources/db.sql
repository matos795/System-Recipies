CREATE TABLE products (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    create_date DATE NOT NULL,
    last_update_date TIMESTAMP
);

CREATE TABLE recipes (
id INT PRIMARY KEY,
last_update_date TIMESTAMP,
description VARCHAR(100),
amount INT,
CONSTRAINT fk_recipe_product FOREIGN KEY (id) REFERENCES products(id)
);