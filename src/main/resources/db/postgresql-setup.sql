-- OFDS - PostgreSQL Setup Script
-- Run the CREATE DATABASE statement while connected to the default
-- PostgreSQL database, then connect to ofds_db before starting the app.

CREATE DATABASE ofds_db;

-- Hibernate (ddl-auto=update) creates the application tables automatically
-- when the Spring Boot application starts.
