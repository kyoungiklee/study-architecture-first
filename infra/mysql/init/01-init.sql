-- ------------------------------------------------------------
-- Nexus Pay - MySQL bootstrap (dev/local)
-- Policy:
--   - Single DB user for all services (dev only)
--   - Credentials: nexuspay / nexus
--   - 6 databases (one per service)
-- ------------------------------------------------------------
-- 1) Databases
CREATE DATABASE IF NOT EXISTS membership_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS money_db      CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS banking_db    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS remittance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS payment_db    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS settlement_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 2) Single application user (dev/local standard)
CREATE USER IF NOT EXISTS 'nexuspay'@'%' IDENTIFIED BY 'nexus';

-- 3) Grants (minimal, but migration-friendly)
-- Includes DDL for Flyway/Liquibase in dev/local.
GRANT
  SELECT, INSERT, UPDATE, DELETE,
  CREATE, ALTER, DROP, INDEX, REFERENCES
ON membership_db.* TO 'nexuspay'@'%';
GRANT
  SELECT, INSERT, UPDATE, DELETE,
  CREATE, ALTER, DROP, INDEX, REFERENCES
ON money_db.* TO 'nexuspay'@'%';
GRANT
  SELECT, INSERT, UPDATE, DELETE,
  CREATE, ALTER, DROP, INDEX, REFERENCES
ON banking_db.* TO 'nexuspay'@'%';
GRANT
  SELECT, INSERT, UPDATE, DELETE,
  CREATE, ALTER, DROP, INDEX, REFERENCES
ON remittance_db.* TO 'nexuspay'@'%';
GRANT
  SELECT, INSERT, UPDATE, DELETE,
  CREATE, ALTER, DROP, INDEX, REFERENCES
ON payment_db.* TO 'nexuspay'@'%';
GRANT
  SELECT, INSERT, UPDATE, DELETE,
  CREATE, ALTER, DROP, INDEX, REFERENCES
ON settlement_db.* TO 'nexuspay'@'%';
FLUSH PRIVILEGES;
