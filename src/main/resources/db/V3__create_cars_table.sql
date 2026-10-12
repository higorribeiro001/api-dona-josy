CREATE TABLE cars (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

      user_id UUID NOT NULL,
      product_id UUID NOT NULL,

      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT fk_cars_user
          FOREIGN KEY (user_id)
              REFERENCES users(id),

      CONSTRAINT fk_cars_product
          FOREIGN KEY (product_id)
              REFERENCES product(id)
);