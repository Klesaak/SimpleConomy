UPDATE %tableName% SET coins = IF (? > coins, 0, coins - ?) WHERE playerName = ? LIMIT 1;