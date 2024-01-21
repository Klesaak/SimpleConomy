CREATE TABLE IF NOT EXISTS `%tableName%` (
            playerName VARCHAR(16) NOT NULL UNIQUE,
            money BIGINT DEFAULT 0,
            coins BIGINT DEFAULT 0,
            -- Indexes
            INDEX (money DESC),
            INDEX (coins DESC)) ENGINE = INNODB;