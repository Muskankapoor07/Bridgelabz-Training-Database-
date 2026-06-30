CREATE TABLE Departments (
    department_id SERIAL  PRIMARY KEY,
    department_name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO Departments (department_name)
VALUES('Sales'),('Engineering'),('Human Resources');

SELECT * FROM Departments;

CREATE TABLE Employees (
    employee_id SERIAL NOT NULL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    department_id INT,
    salary DECIMAL(10,2) NOT NULL,
    joining_date DATE NOT NULL,
    FOREIGN KEY (department_id)
    REFERENCES Departments(department_id)
);

INSERT INTO Employees
(first_name, last_name, department_id, salary, joining_date)
VALUES
('Rahul', 'Sharma', 1, 50000, '2024-01-15'),
('Priya', 'Verma', 2, 65000, '2023-11-20'),
('Amit', 'Singh', 2, 70000, '2022-08-10'),
('Neha', 'Gupta', 3, 45000, '2024-03-05');

SELECT * FROM Employees;

CREATE TABLE Projects (
    project_id SERIAL NOT NULL PRIMARY KEY,
    project_name VARCHAR(100) NOT NULL,
    department_id INT,
    status VARCHAR(20) DEFAULT 'ongoing',
    FOREIGN KEY (department_id)
    REFERENCES Departments(department_id)
);

INSERT INTO Projects
(project_name, department_id)
VALUES
('CRM System', 1),
('AI Chatbot', 2),
('HR Portal', 3);

SELECT * FROM Projects;

--1.
SELECT * FROM Employees;
--2.
INSERT INTO Departments (department_name)
VALUES ('Human Resources');
--3.
INSERT INTO Employees
(first_name, last_name, department_id, salary, joining_date)
VALUES
('renu', 'Sharma', 3, 50000, '2024-01-14');

UPDATE Employees
SET salary = 60000
WHERE employee_id = 5;

--4.
INSERT INTO Projects
(project_name, department_id)
VALUES
('Outdated Project', 2);

DELETE FROM Projects
WHERE project_name = 'Outdated Project';

--5.
SELECT first_name, last_name
FROM Employees
WHERE joining_date > '2020-12-31';

--6.
SELECT * FROM Employees
WHERE salary BETWEEN 40000 AND 70000;

--7.
SELECT * FROM Employees e
JOIN Departments d
ON e.department_id = d.department_id
WHERE d.department_name = 'Sales';

--8.
SELECT * FROM Employees
WHERE first_name LIKE 'A%';

--9.
SELECT * FROM Employees
ORDER BY salary DESC
LIMIT 3;

--10.
SELECT * FROM Projects
WHERE status != 'completed';

--11.
SELECT e.employee_id, e.first_name, e.last_name, d.department_name
FROM Employees e
JOIN Departments d
ON e.department_id = d.department_id;

--12.
SELECT p.project_id, p.project_name, d.department_name,  p.status
FROM Projects p
JOIN Departments d
ON p.department_id = d.department_id;

--13.
