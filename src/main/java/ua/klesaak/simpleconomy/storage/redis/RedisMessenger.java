package ua.klesaak.simpleconomy.storage.redis;

import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.RedisClient;
import ua.klesaak.simpleconomy.configurations.ConfigFile;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.PlayerData;
import ua.klesaak.simpleconomy.storage.redis.messenger.MessageData;

import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;

public class RedisMessenger implements AutoCloseable {
    public static final UUID SERVER_UUID = UUID.randomUUID();

    private final SimpleEconomyManager manager;
    private final ForkJoinPool moneyPool = new ForkJoinPool();
    private final RedisConfig redisConfig;
    private final Subscription subscription;
    private boolean closing = false;

    public RedisMessenger(SimpleEconomyManager manager, RedisConfig redisConfig) {
        this.manager = manager;
        this.redisConfig = redisConfig;
        this.subscription = new Subscription();
        this.moneyPool.execute(this.subscription);
    }


    public void publishMoneyMessage(MessageData messageData) {
        try {
            messageData.setUuid(SERVER_UUID);
            this.redisConfig.getPusSubRedisClient().publish(this.redisConfig.getBalanceKey(), messageData.toJson());
        } catch (Exception e) {
            throw new RuntimeException("Error while publish message", e);
        }
    }

    public void publishCoinsMessage(MessageData messageData) {
        try {
            messageData.setUuid(SERVER_UUID);
            this.redisConfig.getPusSubRedisClient().publish(this.redisConfig.getCoinsKey(), messageData.toJson());
        } catch (Exception e) {
            throw new RuntimeException("Error while publish message", e);
        }
    }

    @Override
    public void close() {
        this.closing = true;
        this.subscription.unsubscribe();
        this.moneyPool.shutdownNow();
    }

    private class Subscription extends JedisPubSub implements Runnable {

        @Override
        public void run() {
            while (!RedisMessenger.this.closing && !Thread.currentThread().isInterrupted()) {

                try (RedisClient jedis = RedisMessenger.this.redisConfig.getPusSubRedisClient()) {

                    // BLOCKING CALL
                    jedis.subscribe(this, RedisMessenger.this.redisConfig.getBalanceKey());

                } catch (Exception e) {

                    if (RedisMessenger.this.closing) {
                        return;
                    }

                    RedisMessenger.this.manager.getPlugin().getLogger().log(Level.SEVERE, "Redis Money pub-sub connection dropped, reconnecting: " + e.getMessage());

                    // анти-спам reconnect
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }

        @Override
        public void onMessage(String channel, String message) {
            if (channel.equals(RedisMessenger.this.redisConfig.getBalanceKey())) {
                MessageData messageData = MessageData.fromJson(message);
                if (messageData.getUuid().equals(SERVER_UUID)) return;
                String playerName = messageData.getPlayerName();
                PlayerData playerData = RedisMessenger.this.manager.getStorage().getPlayerData(playerName);
                if (playerData == null) return; /// проверяем если игрок онлайн(находится в кеше то обновляем)
                double amount = messageData.getAmount();
                ConfigFile configFile = RedisMessenger.this.manager.getConfigFile();
                if (amount > configFile.getMaxBalance() || amount < 0) return;
                playerData.setMoney(amount);
            }

            if (channel.equals(RedisMessenger.this.redisConfig.getCoinsKey())) {
                MessageData messageData = MessageData.fromJson(message);
                if (messageData.getUuid().equals(SERVER_UUID)) return;
                String playerName = messageData.getPlayerName();
                PlayerData playerData = RedisMessenger.this.manager.getStorage().getPlayerData(playerName);
                if (playerData == null) return; /// проверяем если игрок онлайн(находится в кеше то обновляем)
                ConfigFile configFile = RedisMessenger.this.manager.getConfigFile();
                int amount = (int) messageData.getAmount();
                if (amount > configFile.getMaxCoins() || amount < 0) return;
                playerData.setCoins(amount);
            }
        }
    }
}