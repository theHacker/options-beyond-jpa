CREATE TABLE pizza
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(48)                             NOT NULL
);

CREATE TABLE pizza_topping
(
    pizza_id BIGINT      NOT NULL,
    topping  VARCHAR(32) NOT NULL,
    PRIMARY KEY (pizza_id, topping),
    CONSTRAINT fk_pizza_topping_pizza FOREIGN KEY (pizza_id) REFERENCES pizza (id)
);

CREATE TABLE customer
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(32)                             NOT NULL,
    last_name  VARCHAR(32)                             NOT NULL
);

CREATE TABLE shop_order
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    customer_id BIGINT                                  NOT NULL,
    date        DATE                                    NOT NULL,
    CONSTRAINT fk_shop_order_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE TABLE shop_order_item
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    shop_order_id BIGINT                                  NOT NULL,
    pizza_id      BIGINT                                  NOT NULL,
    amount        INT                                     NOT NULL,
    UNIQUE (shop_order_id, pizza_id),
    CONSTRAINT fk_shop_order_item_shop_order FOREIGN KEY (shop_order_id) REFERENCES shop_order (id),
    CONSTRAINT fk_shop_order_item_pizza FOREIGN KEY (pizza_id) REFERENCES pizza (id)
);
