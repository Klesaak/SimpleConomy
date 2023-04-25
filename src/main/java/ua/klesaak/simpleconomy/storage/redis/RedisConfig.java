package ua.klesaak.simpleconomy.storage.redis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisConfig {
    String address, password;
    int port, database;

    public RedisConfig(ConfigurationSection configurationSection) {
        this.address = configurationSection.getString("host");
        this.port = configurationSection.getInt("port");
        this.database = configurationSection.getInt("database");
        this.password = configurationSection.getString("password");
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
            return pool.getResource();
        }

        @Override
        public void close() {
            pool.destroy();
        }
    }
}
