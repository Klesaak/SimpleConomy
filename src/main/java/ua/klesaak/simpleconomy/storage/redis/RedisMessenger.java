package ua.klesaak.simpleconomy.storage.redis;

import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import ua.klesaak.simpleconomy.configurations.ConfigFile;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.PlayerData;
import ua.klesaak.simpleconomy.storage.redis.messenger.MessageData;

import java.util.UUID;

public class RedisMessenger implements AutoCloseable {
    public static final UUID SERVER_UUID = UUID.randomUUID();

    private final SimpleEconomyManager manager;
    private final RedisConfig redisConfig;

    private final StatefulRedisPubSubConnection<String, String> pubSubConnection; //client

    public RedisMessenger(SimpleEconomyManager manager, RedisConfig redisConfig) {
        this.manager = manager;
        this.redisConfig = redisConfig;
        this.pubSubConnection = redisConfig.getRedisClient().connectPubSub();

        this.pubSubConnection.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) { ///todo async
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
        });
    }


    public void publishMoneyMessage(MessageData messageData) {
        try {
            messageData.setUuid(SERVER_UUID);
            this.redisConfig.getStatefulRedisConnection().async().publish(this.redisConfig.getBalanceKey(), messageData.toJson());
        } catch (Exception e) {
            throw new RuntimeException("Error while publish message", e);
        }
    }

    public void publishCoinsMessage(MessageData messageData) {
        try {
            messageData.setUuid(SERVER_UUID);
            this.redisConfig.getStatefulRedisConnection().async().publish(this.redisConfig.getCoinsKey(), messageData.toJson());
        } catch (Exception e) {
            throw new RuntimeException("Error while publish message", e);
        }
    }

    @Override
    public void close() {
        this.pubSubConnection.close();
    }
}