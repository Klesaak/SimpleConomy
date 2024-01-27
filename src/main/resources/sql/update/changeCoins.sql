INSERT INTO %tableName% (playername, coins) VALUES (?, GREATEST(?, 0))
ON DUPLICATE KEY UPDATE coins = GREATEST(coins + ?, 0);