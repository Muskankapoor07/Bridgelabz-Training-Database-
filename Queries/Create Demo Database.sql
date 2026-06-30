CREATE TABLE categories(
category_id SERIAL NOT NULL PRIMARY KEY,
category_name VARCHAR(255),
description VARCHAR(255)
);

INSERT INTO categories(category_name, description)
VALUES('Beverages','soft drinks, coffee, tea'),
('Dairy product','milk, cheese, butter'),('Fruits','apple, banana, mango');

SELECT * FROM categories;

CREATE TABLE customers(
customer_id SERIAL NOT NULL PRIMARY KEY,
customer_name VARCHAR(255),
contact_name VARCHAR(255),
city VARCHAR(255),
country VARCHAR(255)
);

INSERT INTO customers(customer_name, contact_name, city, country)
VALUES('mk','maria','agra','india'),
('gk','alex','chickago','USA'),
('ym','jelly','london','uk');


SELECT * FROM customers;