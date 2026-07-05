-- ============================================================
--  Contact Manager Pro  –  Database Setup Script
--  Run this if auto-init fails, or to add sample data
-- ============================================================

CREATE DATABASE IF NOT EXISTS contact_manager_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE contact_manager_db;

CREATE TABLE IF NOT EXISTS contacts (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(120)  NOT NULL,
    phone       VARCHAR(20)   NOT NULL,
    email       VARCHAR(120),
    category    VARCHAR(50)   DEFAULT 'General',
    notes       TEXT,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Optional: sample data ──────────────────────────────────
INSERT IGNORE INTO contacts (name, phone, email, category, notes) VALUES
    ('Alice Johnson',   '+1-555-0101', 'alice@example.com',   'Family',  'Sister'),
    ('Bob Martinez',    '+1-555-0102', 'bob.m@work.com',      'Work',    'Project lead'),
    ('Carol White',     '+1-555-0103', 'carol@friends.net',   'Friends', 'College buddy'),
    ('David Lee',       '+1-555-0104', 'david.lee@corp.com',  'Work',    'CTO'),
    ('Emma Davis',      '+1-555-0105', 'emma@family.org',     'Family',  'Mom'),
    ('Frank Wilson',    '+1-555-0106', NULL,                  'General', NULL),
    ('Grace Kim',       '+1-555-0107', 'grace@vip.com',       'VIP',     'Key client'),
    ('Henry Brown',     '+1-555-0108', 'henry@friends.co',    'Friends', 'Gym partner');

SELECT CONCAT('✓ Setup complete. Rows inserted: ', COUNT(*)) AS status FROM contacts;
