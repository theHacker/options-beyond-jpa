INSERT INTO pizza (id, name)
VALUES
    (1, 'Margherita Classica'),
    (2, 'Hähnchen'),
    (3, 'Bombay');

ALTER TABLE pizza ALTER COLUMN id RESTART WITH 4;

INSERT INTO pizza_topping (pizza_id, topping)
VALUES
    (1, 'Tomatensoße'), (1, 'Käse'),
    (2, 'Hähnchen'), (2, 'Knoblauch'), (2, 'scharf'),
    (3, 'Hähnchen'), (3, 'Ananas'), (3, 'Peperoni'), (3, 'Curry'), (3, 'scharf');

INSERT INTO customer (id, first_name, last_name)
VALUES
    (1, 'Max', 'Mustermann'),
    (2, 'Anja', 'Musterfrau'),
    (3, 'Kim', 'Musterdivers');

ALTER TABLE customer ALTER COLUMN id RESTART WITH 4;

INSERT INTO shop_order (id, customer_id, date)
VALUES
    (1, 2, '2024-04-02'),
    (2, 2, '2024-04-05'),
    (3, 1, '2024-04-05'),
    (4, 3, '2024-04-12');

ALTER TABLE shop_order ALTER COLUMN id RESTART WITH 5;

INSERT INTO shop_order_item (id, shop_order_id, pizza_id, amount)
VALUES
    (1, 1, 2, 1), (2, 1, 1, 2),
    (3, 2, 3, 1), (4, 2, 2, 1),
    (5, 3, 1, 1),
    (6, 4, 2, 1), (7, 4, 1, 5), (8, 4, 3, 2);

ALTER TABLE shop_order_item ALTER COLUMN id RESTART WITH 9;
