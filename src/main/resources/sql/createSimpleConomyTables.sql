CREATE TABLE IF NOT EXISTS `%table%` (
    `player_name` VARCHAR(16) NOT NULL,
    `money_balance` BIGINT DEFAULT 0,
    `coins_balance` BIGINT DEFAULT 0,

    PRIMARY KEY (`player_name`),
    INDEX `idx_player_name` (`player_name`),
    INDEX `idx_money_balance` (`money_balance`),
    INDEX `idx_coins_balance` (`coins_balance`)) ENGINE=InnoDB;