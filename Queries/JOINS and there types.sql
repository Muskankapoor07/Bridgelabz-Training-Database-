CREATE TABLE students(
student_id SERIAL NOT NULL PRIMARY KEY,
student_name VARCHAR(50),
city VARCHAR(50)
);

INSERT INTO students (student_name,city)
VALUES('Muskan','Agra'),('Priya','Delhi'),
('Karan', 'Pune'),
('Priya', 'Jaipur');

SELECT * FROM students;

CREATE TABLE results (
    result_id INT PRIMARY KEY,
    student_id INT,
    marks INT
);

INSERT INTO results
VALUES
(101, 1, 90),
(102, 2, 85),
(103, 4, 78),
(104, 6, 95);

SELECT * FROM results;

SELECT * FROM students s
INNER JOIN results r
ON s.student_id = r.student_id;

SELECT * FROM students s
LEFT JOIN results r
ON s.student_id = r.student_id;


SELECT * FROM students s
FULL JOIN results r
ON s.student_id = r.student_id;

--Find students who have not received results
SELECT * FROM students s  
LEFT JOIN results r
ON s.student_id = r.student_id
WHERE r.student_id IS NULL;

--Find result records whose student details are missing
SELECT * FROM students s
RIGHT JOIN results r
ON s.student_id = r.student_id
WHERE s.student_id IS NULL;

SELECT * FROM students
CROSS JOIN results;

CREATE TABLE employees (
    emp_id INT PRIMARY KEY,
    emp_name VARCHAR(50),
    manager_id INT
);

INSERT INTO employees
VALUES
(1, 'Rahul', NULL),
(2, 'Priya', 1),
(3, 'Amit', 1),
(4, 'Neha', 2);

SELECT * FROM employees;

SELECT e.emp_name AS Employee,
m.emp_name AS Manager
FROM employees e
JOIN employees m
ON e.manager_id = m.emp_id;
