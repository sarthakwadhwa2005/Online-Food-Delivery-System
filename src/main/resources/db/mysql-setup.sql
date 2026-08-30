-- ============================================================
-- OFDS - MySQL Setup Script
-- Run this ONCE before starting the Spring Boot application
-- ============================================================

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS ofds_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. (Optional) Create a dedicated user instead of using root
--    Uncomment and edit if you want a separate DB user:
-- CREATE USER IF NOT EXISTS 'ofds_user'@'localhost' IDENTIFIED BY 'ofds_pass';
-- GRANT ALL PRIVILEGES ON ofds_db.* TO 'ofds_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 3. Use the database
USE ofds_db;

-- ============================================================
-- NOTE: Hibernate (ddl-auto=update) will create all tables
-- automatically when the app starts for the first time.
-- You do NOT need to create tables manually.
-- ============================================================

-- Verify setup
SELECT 'ofds_db created successfully!' AS status;
