-- ==========================================================
-- DROP OLD OBJECTS
-- ==========================================================

DROP TRIGGER IF EXISTS trg_log_salary_change ON employees;

DROP FUNCTION IF EXISTS log_salary_change();

DROP FUNCTION IF EXISTS get_total_payroll_by_dept(VARCHAR);

DROP TABLE IF EXISTS payroll_audit;

DROP TABLE IF EXISTS employee_departments;

DROP TABLE IF EXISTS employees;

DROP TABLE IF EXISTS users;

-- ==========================================================
-- USERS TABLE
-- ==========================================================

CREATE TABLE users(

                      id SERIAL PRIMARY KEY,

                      username VARCHAR(50) UNIQUE NOT NULL,

                      password VARCHAR(64) NOT NULL,

                      email VARCHAR(100) UNIQUE NOT NULL,

                      role VARCHAR(20) NOT NULL
                          CHECK(role IN ('ADMIN','USER')),

                      created_at TIMESTAMP WITH TIME ZONE
                          DEFAULT CURRENT_TIMESTAMP

);

-- ==========================================================
-- EMPLOYEES TABLE
-- ==========================================================

CREATE TABLE employees(

                          id SERIAL PRIMARY KEY,

                          name VARCHAR(100) NOT NULL,

                          profile_image VARCHAR(100) NOT NULL,

                          gender VARCHAR(10) NOT NULL
                              CHECK(gender IN ('Male','Female')),

                          salary NUMERIC(10,2) NOT NULL
                              CHECK(salary>=0),

                          start_date DATE NOT NULL,

                          notes TEXT,

                          created_by INTEGER
                              REFERENCES users(id)
                                                ON DELETE SET NULL,

                          created_at TIMESTAMP WITH TIME ZONE
                              DEFAULT CURRENT_TIMESTAMP

);

-- ==========================================================
-- EMPLOYEE DEPARTMENTS
-- ==========================================================

CREATE TABLE employee_departments(

                                     employee_id INTEGER
                                         REFERENCES employees(id)
                                             ON DELETE CASCADE,

                                     department VARCHAR(50) NOT NULL,

                                     PRIMARY KEY(employee_id,department)

);

-- ==========================================================
-- PAYROLL AUDIT TABLE
-- ==========================================================

CREATE TABLE payroll_audit(

                              id SERIAL PRIMARY KEY,

                              employee_id INTEGER NOT NULL,

                              action_type VARCHAR(10) NOT NULL,

                              old_salary NUMERIC(10,2),

                              new_salary NUMERIC(10,2),

                              changed_by VARCHAR(50),

                              changed_at TIMESTAMP WITH TIME ZONE
                                  DEFAULT CURRENT_TIMESTAMP

);

-- ==========================================================
-- INDEXES
-- ==========================================================

CREATE UNIQUE INDEX idx_users_username
    ON users(username);

CREATE INDEX idx_emp_dept_id
    ON employee_departments(employee_id);


-- ==========================================================
-- FUNCTION : LOG SALARY CHANGE
-- ==========================================================

CREATE OR REPLACE FUNCTION log_salary_change()

RETURNS TRIGGER AS
$$

DECLARE

v_user VARCHAR(50);

BEGIN

    IF(TG_OP='DELETE') THEN

SELECT username
INTO v_user
FROM users
WHERE id=OLD.created_by;

INSERT INTO payroll_audit
(
    employee_id,
    action_type,
    old_salary,
    new_salary,
    changed_by
)
VALUES
    (
        OLD.id,
        'DELETE',
        OLD.salary,
        NULL,
        COALESCE(v_user,'UNKNOWN')
    );

RETURN OLD;

ELSIF(TG_OP='UPDATE') THEN

        IF OLD.salary <> NEW.salary THEN

SELECT username
INTO v_user
FROM users
WHERE id=NEW.created_by;

INSERT INTO payroll_audit
(
    employee_id,
    action_type,
    old_salary,
    new_salary,
    changed_by
)
VALUES
    (
        NEW.id,
        'UPDATE',
        OLD.salary,
        NEW.salary,
        COALESCE(v_user,'UNKNOWN')
    );

END IF;

RETURN NEW;

ELSIF(TG_OP='INSERT') THEN

SELECT username
INTO v_user
FROM users
WHERE id=NEW.created_by;

INSERT INTO payroll_audit
(
    employee_id,
    action_type,
    old_salary,
    new_salary,
    changed_by
)
VALUES
    (
        NEW.id,
        'INSERT',
        NULL,
        NEW.salary,
        COALESCE(v_user,'UNKNOWN')
    );

RETURN NEW;

END IF;

RETURN NULL;

END;

$$
LANGUAGE plpgsql;

-- ==========================================================
-- TRIGGER
-- ==========================================================

CREATE TRIGGER trg_log_salary_change

    AFTER INSERT OR UPDATE OR DELETE

                    ON employees

                        FOR EACH ROW

                        EXECUTE FUNCTION log_salary_change();

-- ==========================================================
-- STORED FUNCTION
-- ==========================================================

CREATE OR REPLACE FUNCTION
get_total_payroll_by_dept(p_dept VARCHAR)

RETURNS NUMERIC AS
$$

DECLARE

total NUMERIC;

BEGIN

SELECT SUM(e.salary)

INTO total

FROM employees e

         JOIN employee_departments ed

              ON e.id=ed.employee_id

WHERE ed.department=p_dept;

RETURN COALESCE(total,0);

END;

$$
LANGUAGE plpgsql;

-- ==========================================================
-- SEED DATA
-- ==========================================================

INSERT INTO users
(username,password,email,role)

VALUES

    (
        'arpit',
        '6111eb4fbf360b283e2170f049d392cfdf4503d0e5844253a917dd5f8de14c50',
        'admin@gmail.com',
        'ADMIN'
    ),

    (
        'user',
        '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb',
        'user@gmail.com',
        'USER'
    );

INSERT INTO employees
(
    name,
    profile_image,
    gender,
    salary,
    start_date,
    notes,
    created_by
)

VALUES

    (
        'Amarpa Keerthi Kumar',
        'ellipse-1.png',
        'Male',
        60000,
        CURRENT_DATE,
        'Seed Employee',
        1
    );

INSERT INTO employee_departments

VALUES

    (1,'HR'),

    (1,'Sales'),

    (1,'Finance');