package ua.klesaak.simpleconomy.storage.redis;

import lombok.Getter;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

@Getter
public class RedisConfig {
    private final String address, password, balanceKey, coinsKey;
    private final int port, database;

    public RedisConfig(ConfigurationSection configurationSection) {
        this.address = configurationSection.getString("host");
        this.port = configurationSection.getInt("port");
        this.database = configurationSection.getInt("database");
        this.password = configurationSection.getString("password");
        this.balanceKey = configurationSection.getString("balanceKey");
        this.coinsKey = configurationSection.getString("coinsKey");
    }

    public RedisPool newRedisPool() throws JedisException {
        return new RedisPool(this.address, this.port, this.password);
    }

    public static class RedisPool implements AutoCloseable {
        private final JedisPool pool;

        public RedisPool(String host, int port, String pass) {
            val jpc = new JedisPoolConfig();
            jpc.setLifo(false);
            jpc.setTestOnBorrow(true);
            jpc.setMinIdle(3);
            jpc.setMaxTotal(500);
            this.pool = new JedisPool(jpc, host, port, 30000, pass == null || pass.isEmpty() ? null : pass);
        }

        public Jedis getRedis() {
            return this.pool.getResource();
        }

        @Override
        public void close() {
            if (this.pool != null) this.pool.destroy();
        }
    }
}
