package ua.klesaak.simpleconomy.storage.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;;

@Getter
public class RedisConfig implements AutoCloseable {
    private final String address, password, balanceKey, coinsKey;
    private final int port, database, threads;

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> statefulRedisConnection;

    public RedisConfig(ConfigurationSection configurationSection) {
        this.address = configurationSection.getString("host");
        this.port = configurationSection.getInt("port");
        this.database = configurationSection.getInt("database");
        this.password = configurationSection.getString("password");
        this.balanceKey = configurationSection.getString("balanceKey");
        this.coinsKey = configurationSection.getString("coinsKey");
        this.threads = configurationSection.getInt("threads");

        this.redisClient = this.createRedisClient();
        this.statefulRedisConnection = this.redisClient.connect();
    }

    public RedisClient createRedisClient() {
        return RedisClient.create(RedisURI.builder()
                .withHost(this.address)
                .withPort(this.port)
                .withDatabase(this.database)
                .withPassword(this.password)
                .build());
    }

    @Override
    public void close() {
        if (this.redisClient != null) {
            this.redisClient.close();
        }
        if (this.statefulRedisConnection != null) {
            this.statefulRedisConnection.close();
        }
    }
}