CREATE TABLE teachers (
    name VARCHAR(50)
);

INSERT INTO teachers
VALUES
('Rahul'),
('Priya'),
('Amit');

SELECT * FROM teachers;

CREATE TABLE students_list (
    name VARCHAR(50)
);
INSERT INTO students_list
VALUES
('Priya'),
('Neha'),
('Amit'),
('Karan');

SELECT * FROM students_list;

SELECT name FROM teachers
UNION
SELECT name FROM students_list
ORDER BY name;

SELECT name FROM teachers
UNION ALL
SELECT name FROM students_list
ORDER BY name;

CREATE TABLE employes (
    emp_id INT PRIMARY KEY,
    emp_name VARCHAR(50),
    department VARCHAR(30),
    city VARCHAR(30),
    salary DECIMAL(10,2)
);
INSERT INTO employes
VALUES
(1, 'Rahul', 'IT', 'Delhi', 50000),
(2, 'Priya', 'HR', 'Mumbai', 40000),
(3, 'Amit', 'IT', 'Delhi', 60000),
(4, 'Neha', 'HR', 'Pune', 45000),
(5, 'Karan', 'IT', 'Mumbai', 55000),
(6, 'Riya', 'Sales', 'Delhi', 35000),
(7, 'Ankit', 'Sales', 'Pune', 38000),
(8, 'Sneha', 'IT', 'Delhi', 65000);

SELECT department
FROM employes
GROUP BY department;

SELECT department
FROM employes
GROUP BY department
ORDER BY department;

SELECT department, COUNT(*)
FROM employes
GROUP BY department;

SELECT department, SUM(salary)
FROM employes
GROUP BY department;

SELECT department, AVG(salary)
FROM employes
GROUP BY department;

SELECT s.student_name, COUNT(r.result_id)
FROM students s
JOIN results r
ON s.student_id = r.student_id
GROUP BY s.student_name;

SELECT department, COUNT(*)
FROM employes
GROUP BY department
HAVING COUNT(*) > 2;