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

SELECT customer_name, country FROM customers;

SELECT DISTINCT country FROM customers;

SELECT COUNT(DISTINCT COUNTRY) FROM customers;

SELECT * FROM customers
WHERE city = 'london';

SELECT * FROM  customers
WHERE customer_id = 3;

SELECT * FROM  customers
WHERE customer_id > 1;


CREATE TABLE products(
product_id SERIAL NOT NULL PRIMARY KEY,
product_name VARCHAR(255),
category_id INT,
unit VARCHAR(255),
price DECIMAL(10,2)
);

INSERT INTO products(product_name,category_id,unit,price)
VALUES('Chais', 1, '10 boxes x 20 bags', 18),
  ('Chang', 1, '24 - 12 oz bottles', 19),
  ('Aniseed Syrup', 2, '12 - 550 ml bottles', 10),
  ('Chef Antons Cajun Seasoning', 2, '48 - 6 oz jars', 22),
  ('Chef Antons Gumbo Mix', 2, '36 boxes', 21.35),
  ('Grandmas Boysenberry Spread', 2, '12 - 8 oz jars', 25);

  SELECT* FROM products;

  SELECT * FROM products
  ORDER BY price;

  SELECT * FROM products
  ORDER BY price DESC;

  SELECT * FROM products
  ORDER BY product_name;
  
  SELECT * FROM products
  ORDER BY product_name DESC;

  SELECT * FROM products 
  LIMIT 3;

  SELECT * FROM products 
  LIMIT 5 OFFSET 2 ;

  SELECT MIN(price) FROM products;

  SELECT MAX(price) FROM products;
  