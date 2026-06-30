CREATE TABLE orders (
  order_id SERIAL NOT NULL PRIMARY KEY,
  customer_id INT,
  order_date DATE
);

INSERT INTO orders (order_id, customer_id, order_date)
VALUES
  (10248, 90, '2021-07-04'),
  (10249, 81, '2021-07-05'),
  (10250, 34, '2021-07-08'),
  (10251, 84, '2021-07-08'),
  (10252, 76, '2021-07-09'),
  (10253, 34, '2021-07-10'),
  (10254, 14, '2021-07-11'),
  (10255, 68, '2021-07-12'),
  (10256, 88, '2021-07-15'),
  (10257, 35, '2021-07-16'),
  (10258, 20, '2021-07-17'),
  (10259, 13, '2021-07-18'),
  (10260, 55, '2021-07-19'),
  (10261, 61, '2021-07-19'),
  (10262, 65, '2021-07-22'),
  (10263, 20, '2021-07-23'),
  (10264, 24, '2021-07-24'),
  (10265, 7, '2021-07-25'),
  (10266, 87, '2021-07-26'),
  (10267, 25, '2021-07-29'),
  (10268, 33, '2021-07-30'),
  (10269, 89, '2021-07-31'),
  (10270, 87, '2021-08-01'),
  (10271, 75, '2021-08-01'),
  (10272, 65, '2021-08-02'),
  (10273, 63, '2021-08-05'),
  (10274, 85, '2021-08-06');

  SELECT * FROM orders;

  DROP TABLE orders;

  DROP TABLE products;



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

  SELECT MIN(price) FROM  products;

  SELECT Max(price) FROM  products;

  SELECT MIN(price) AS lowest_price FROM  products;
  
  SELECT MAX(price) AS high_price FROM  products;

  SELECT COUNT(product_id) FROM products;

  SELECT COUNT(customer_id) FROM customers
  WHERE city = 'london';

  SELECT * FROM customers;

  INSERT INTO customers(customer_name,contact_name,city,country)
  VALUES('jk','pihu','london','uk');

  CREATE TABLE order_details (
  order_detail_id SERIAL NOT NULL PRIMARY KEY,
  order_id INT,
  product_id INT,
  quantity INT
);

INSERT INTO order_details (order_id, product_id, quantity)
VALUES
  (10248, 11, 12),
  (10248, 42, 10),
  (10248, 72, 5),
  (10249, 14, 9),
  (10249, 51, 40),
  (10250, 41, 10),
  (10250, 51, 35),
  (10250, 65, 15),
  (10251, 22, 6),
  (10251, 57, 15),
  (10251, 65, 20),
  (10252, 20, 40),
  (10252, 33, 25),
  (10252, 60, 40);

  SELECT * FROM order_details;

  SELECT SUM(quantity) FROM order_details;

  SELECT COUNT(order_detail_id) FROM order_details;

  SELECT AVG(PRICE) FROM PRODUCTS;

  SELECT SUM(price) FROM products;

  SELECT * FROM customers;

  SELECT AVG(price)::NUMERIC(10,2) FROM products;
  
  SELECT * FROM customers
  WHERE customer_name LIKE 'S%';

 INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('ABC Traders', 'Rahul Sharma', 'Delhi', 'India');

INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('Tech Solutions', 'Priya Verma', 'Mumbai', 'India');

INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('Global Mart', 'Amit Kumar', 'Bangalore', 'India');

INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('Sunrise Foods', 'Neha Singh', 'Jaipur', 'India');

INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('Green Farms', 'Rohit Gupta', 'Lucknow', 'India');

INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('Skyline Pvt Ltd', 'Anjali Mehta', 'Pune', 'India');

INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('Ocean Exports', 'Vikas Jain', 'Chennai', 'India');

INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('Bright Electronics', 'Sneha Kapoor', 'Hyderabad', 'India');

INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('Royal Furnitures', 'Arjun Malhotra', 'Kolkata', 'India');

INSERT INTO customers (customer_name, contact_name, city, country)
VALUES ('Smart Enterprises', 'Karan Patel', 'Ahmedabad', 'India');

SELECT * FROM customers
WHERE customer_name  LIKE '%E%';

SELECT * FROM customers
WHERE customer_name ILIKE '%E%';

SELECT * FROM customers
WHERE customer_name LIKE '%ts'; 

SELECT * FROM customers 
WHERE city LIKE 'l_____';

SELECT * FROM customers
WHERE country IN ('India','uk');

SELECT * FROM customers
WHERE country NOT IN ('India','uk');

SELECT * FROM customers
WHERE customer_id IN (SELECT customer_id FROM orders);

SELECT * FROM customers
WHERE customer_id NOT IN (SELECT customer_id FROM orders);

SELECT * FROM products;

SELECT * FROM products
WHERE price BETWEEN 10 AND 21.35;


CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    student_name VARCHAR(50),
    city VARCHAR(50)
);

INSERT INTO students (student_name, city)
VALUES
('Aman', 'Delhi'),
('Riya', 'Mumbai'),
('Neha', 'Agra'),
('Karan', 'Pune'),
('Priya', 'Jaipur'),
('Zoya', 'Lucknow');

SELECT * FROM students;

SELECT * FROM students
WHERE student_name BETWEEN 'Neha' AND 'Zoya';

SELECT * FROM students
WHERE student_name BETWEEN 'Neha' AND 'Zoya'
ORDER BY student_name;

SELECT customer_id AS id
FROM customers;

SELECT customer_id id
FROM customers;

SELECT product_name || unit AS product
FROM products;

SELECT product_name || '  ' || unit AS product
FROM products;

SELECT product_name AS "My Great Products"
FROM products;