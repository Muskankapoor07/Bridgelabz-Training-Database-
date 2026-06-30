-- ============================================================
-- 1. DDL STRUCTURE (Data Definition Language)
-- ============================================================
DROP TRIGGER IF EXISTS trg_log_greeting_audit ON greetings;
DROP FUNCTION IF EXISTS log_greeting_audit();
DROP FUNCTION IF EXISTS get_user_greeting_count(VARCHAR);
DROP TABLE IF EXISTS greeting_audit;
DROP TABLE IF EXISTS greetings;
DROP TABLE IF EXISTS users;
-- Users Table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(64) NOT NULL, -- SHA-256 hash output
                       email VARCHAR(100) UNIQUE NOT NULL,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
-- Greetings Table
CREATE TABLE greetings (
                           id SERIAL PRIMARY KEY,
                           message VARCHAR(255) NOT NULL,
                           created_by INTEGER REFERENCES users(id) ON DELETE CASCADE,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
-- Audit Table
CREATE TABLE greeting_audit (
                                id SERIAL PRIMARY KEY,
                                greeting_id INTEGER,
                                action_type VARCHAR(10) NOT NULL,
                                old_message VARCHAR(255),
                                new_message VARCHAR(255),
                                changed_by VARCHAR(50),
                                changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
-- ============================================================
-- 2. INDEXES (Performance Tuning)
-- ============================================================
CREATE UNIQUE INDEX idx_users_username ON users(username);
CREATE INDEX idx_greetings_created_by ON greetings(created_by);
-- ============================================================
-- 3. STORED FUNCTIONS & PROCEDURES (Server-Side Logic)
-- ============================================================
-- Function 1: Computes the count of greetings created by a given user
CREATE OR REPLACE FUNCTION get_user_greeting_count(p_username VARCHAR)
RETURNS INTEGER AS $$
DECLARE
v_count INTEGER;
 v_user_id INTEGER;
BEGIN
SELECT id INTO v_user_id FROM users WHERE username = p_username;
IF v_user_id IS NULL THEN
 RETURN 0;
END IF;

SELECT COUNT(*) INTO v_count FROM greetings WHERE created_by = v_user_id;
RETURN v_count;
END;
$$ LANGUAGE plpgsql;
-- Function 2: Automates Change Logging
CREATE OR REPLACE FUNCTION log_greeting_audit()
RETURNS TRIGGER AS $$
DECLARE
v_user VARCHAR(50);
BEGIN
 IF (TG_OP = 'DELETE') THEN
SELECT username INTO v_user FROM users WHERE id = OLD.created_by;
INSERT INTO greeting_audit (greeting_id, action_type, old_message,
                            new_message, changed_by)
VALUES (OLD.id, 'DELETE', OLD.message, NULL, COALESCE(v_user, 'UNKNOWN'));
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
SELECT username INTO v_user FROM users WHERE id = NEW.created_by;
INSERT INTO greeting_audit (greeting_id, action_type, old_message,
                            new_message, changed_by)
VALUES (NEW.id, 'UPDATE', OLD.message, NEW.message, COALESCE(v_user,
                                                             'UNKNOWN'));
RETURN NEW;
ELSIF (TG_OP = 'INSERT') THEN
SELECT username INTO v_user FROM users WHERE id = NEW.created_by;
INSERT INTO greeting_audit (greeting_id, action_type, old_message,
                            new_message, changed_by)
VALUES (NEW.id, 'INSERT', NULL, NEW.message, COALESCE(v_user, 'UNKNOWN'));
RETURN NEW;
END IF;
RETURN NULL;
END;
$$ LANGUAGE plpgsql;
-- ============================================================
-- 4. TRIGGERS (Auditing Automation)
-- ============================================================
CREATE TRIGGER trg_log_greeting_audit
    AFTER INSERT OR UPDATE OR DELETE ON greetings
    FOR EACH ROW
    EXECUTE FUNCTION log_greeting_audit();
-- ============================================================
-- 5. INITIAL SEED DATA (DML Operations)
-- ============================================================
INSERT INTO users (username, password, email, role) VALUES
                                                        ('admin_kiran',
                                                         '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
                                                         'kiran@example.com', 'ADMIN'),
                                                        ('user_rohan', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3',
                                                         'rohan@example.com', 'USER');
INSERT INTO greetings (message, created_by) VALUES
                                                ('Hello, welcome to our workspace app!', 1),
                                                ('Learning PostgreSQL JDBC raw today!', 2);

