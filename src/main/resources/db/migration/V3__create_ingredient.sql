CREATE TABLE ingredient (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    unit unit NOT NULL,
    recipe_id BIGINT NOT NULL,
    CONSTRAINT fk_ingredient_recipe FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE
);