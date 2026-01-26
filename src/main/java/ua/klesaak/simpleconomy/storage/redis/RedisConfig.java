package ua.klesaak.simpleconomy.storage.redis;

import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.configuration.ConfigurationSection;
import redis.clients.jedis.*;

@Getter
public class RedisConfig implements AutoCloseable {
    private final String address, password, balanceKey, coinsKey;
    private final int port, database;
    private final RedisClient redisClient;

    public RedisConfig(ConfigurationSection configurationSection) {
        this.address = configurationSection.getString("host");
        this.port = configurationSection.getInt("port");
        this.database = configurationSection.getInt("database");
        this.password = configurationSection.getString("password");
        this.balanceKey = configurationSection.getString("balanceKey");
        this.coinsKey = configurationSection.getString("coinsKey");

        HostAndPort hostAndPort = new HostAndPort(this.address, this.port);
        DefaultJedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .timeoutMillis(30_000)
                .database(this.database)
                .password(this.password == null || this.password.isEmpty() ? null : this.password).build();
        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setLifo(false);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setMinIdle(3);
        poolConfig.setMaxTotal(500);
        this.redisClient = RedisClient.builder()
                .poolConfig(poolConfig)
                .clientConfig(clientConfig)
                .hostAndPort(hostAndPort).build();
    }


    @Override
    public void close() {
        if (this.redisClient != null) this.redisClient.close();
    }
}
