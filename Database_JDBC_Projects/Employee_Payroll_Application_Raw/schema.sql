-- ============================================================
-- Employee Payroll Database Schema
-- ============================================================

-- ============================================================
-- DROP OLD OBJECTS
-- ============================================================

DROP TRIGGER IF EXISTS trg_log_salary_change ON employees;

DROP FUNCTION IF EXISTS log_salary_change();

DROP FUNCTION IF EXISTS get_total_payroll_by_dept(VARCHAR);

DROP TABLE IF EXISTS payroll_audit;
DROP TABLE IF EXISTS employee_departments;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS users;

-- ============================================================
-- USERS TABLE
-- ============================================================

CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,

    username   VARCHAR(50) UNIQUE                            NOT NULL,

    password   VARCHAR(64)                                   NOT NULL,

    email      VARCHAR(100) UNIQUE                           NOT NULL,

    role       VARCHAR(20) CHECK (role IN ('ADMIN', 'USER')) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- EMPLOYEES TABLE
-- ============================================================

CREATE TABLE employees
(
    id            SERIAL PRIMARY KEY,

    name          VARCHAR(100)                                     NOT NULL,

    profile_image VARCHAR(100)                                     NOT NULL,

    gender        VARCHAR(10) CHECK (gender IN ('Male', 'Female')) NOT NULL,

    salary        NUMERIC(10, 2) CHECK (salary >= 0)               NOT NULL,

    start_date    DATE                                             NOT NULL,

    notes         TEXT,

    created_by    INTEGER,

    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL
);

-- ============================================================
-- EMPLOYEE DEPARTMENTS TABLE
-- ============================================================

CREATE TABLE employee_departments
(
    employee_id INTEGER,

    department  VARCHAR(50),

    PRIMARY KEY (employee_id, department),

    FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE
);

-- ============================================================
-- PAYROLL AUDIT TABLE
-- ============================================================

CREATE TABLE payroll_audit
(
    id          SERIAL PRIMARY KEY,

    employee_id INTEGER     NOT NULL,

    action_type VARCHAR(10) NOT NULL,

    old_salary  NUMERIC(10, 2),

    new_salary  NUMERIC(10, 2),

    changed_by  VARCHAR(50),

    changed_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE UNIQUE INDEX idx_users_username ON users (username);

CREATE INDEX idx_emp_dept_id ON employee_departments (employee_id);

-- ============================================================
-- STORED FUNCTION
-- Department Wise Payroll
-- ============================================================

CREATE
OR REPLACE FUNCTION get_total_payroll_by_dept(p_dept VARCHAR)
RETURNS NUMERIC
AS
$$
BEGIN

RETURN (SELECT COALESCE(SUM(e.salary), 0)
        FROM employees e
                 JOIN employee_departments d ON e.id = d.employee_id
        WHERE d.department = p_dept);

END;
$$
LANGUAGE plpgsql;

-- ============================================================
-- AUDIT FUNCTION
-- ============================================================

CREATE
OR REPLACE FUNCTION log_salary_change()
RETURNS TRIGGER
AS
$$

DECLARE

v_user VARCHAR(50);

BEGIN

IF
(TG_OP='INSERT')
THEN

SELECT username
INTO v_user
FROM users
WHERE id = NEW.created_by;

INSERT INTO payroll_audit (employee_id,
                           action_type,
                           old_salary,
                           new_salary,
                           changed_by)

VALUES (NEW.id,
        'INSERT',
        NULL,
        NEW.salary,
        COALESCE(v_user, 'UNKNOWN'));

RETURN NEW;

ELSIF
(TG_OP='UPDATE')
THEN

IF OLD.salary<>NEW.salary
THEN

SELECT username
INTO v_user
FROM users
WHERE id = NEW.created_by;

INSERT INTO payroll_audit (employee_id,
                           action_type,
                           old_salary,
                           new_salary,
                           changed_by)

VALUES (NEW.id,
        'UPDATE',
        OLD.salary,
        NEW.salary,
        COALESCE(v_user, 'UNKNOWN'));

END IF;

RETURN NEW;

ELSIF
(TG_OP='DELETE')
THEN

SELECT username
INTO v_user
FROM users
WHERE id = OLD.created_by;

INSERT INTO payroll_audit (employee_id,
                           action_type,
                           old_salary,
                           new_salary,
                           changed_by)

VALUES (OLD.id,
        'DELETE',
        OLD.salary,
        NULL,
        COALESCE(v_user, 'UNKNOWN'));

RETURN OLD;

END IF;

RETURN NULL;

END;

$$
LANGUAGE plpgsql;

-- ============================================================
-- TRIGGER
-- ============================================================

CREATE TRIGGER trg_log_salary_change

    AFTER INSERT OR
UPDATE OR
DELETE

ON employees FOR EACH ROW EXECUTE FUNCTION log_salary_change();

-- ============================================================
-- SEED USERS
-- Passwords:
-- arpit123
-- shyam123
-- ============================================================

INSERT INTO users (username, password, email, role)

VALUES ('arpit',
        '6111eb4fbf360b283e2170f049d392cfdf4503d0e5844253a917dd5f8de14c50',
        'arpit@gmail.com',
        'ADMIN'),

       ('shyam',
        '58797ea04b79bbd341e11089b92ebab015b0053299578f0ad801c8ca79d68db0',
        'shyam@gmail.com',
        'USER');

-- ============================================================
-- SEED EMPLOYEE
-- ============================================================

INSERT INTO employees (name,
                       profile_image,
                       gender,
                       salary,
                       start_date,
                       notes,
                       created_by)

VALUES ('Amarpa Keerthi Kumar',
        'ellipse-1.png',
        'Female',
        10000,
        '2019-10-29',
        'Senior specialist account manager.',
        1);

INSERT INTO employee_departments
VALUES (1, 'Sales'),
       (1, 'HR'),
       (1, 'Finance');

user_id INTEGER UNIQUE REFERENCES users(id),

CREATE TABLE employees (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           profile_image VARCHAR(100) NOT NULL,
                           gender VARCHAR(10) NOT NULL,
                           salary NUMERIC(10,2) NOT NULL,
                           start_date DATE NOT NULL,
                           notes TEXT,

                           user_id INTEGER UNIQUE REFERENCES users(id),

                           created_by INTEGER REFERENCES users(id),

                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE employees
    ADD COLUMN user_id INTEGER;

ALTER TABLE employees
    ADD CONSTRAINT fk_employee_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);

ALTER TABLE employees
    ADD CONSTRAINT uq_employee_user
        UNIQUE(user_id);

UPDATE employees
SET user_id = 2
WHERE id = 1;

SELECT
    id,
    name,
    user_id,
    created_by
FROM employees;

UPDATE employees
SET user_id = 3
WHERE id = 1;

SELECT id, name
FROM employees
ORDER BY id;
SELECT last_value
FROM employees_id_seq;

TRUNCATE TABLE employee_departments RESTART IDENTITY CASCADE;
TRUNCATE TABLE employees RESTART IDENTITY CASCADE;