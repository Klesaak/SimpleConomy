package net.klesaak.simpleconomy.storage;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import net.klesaak.storagelib.api.cache.ScheduledCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.klesaak.simpleconomy.configurations.ConfigFile;
import net.klesaak.simpleconomy.configurations.Message;
import net.klesaak.simpleconomy.manager.SimpleEconomyManager;
import net.klesaak.simpleconomy.storage.mesenger.MessageType;
import net.klesaak.simpleconomy.storage.mesenger.PubSubUpdateMessage;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class AbstractStorage implements AutoCloseable {
    public static final UUID SERVER_UUID = UUID.randomUUID();
    public static final String SCONOMY_UPDATE_CHANNEL = "sc-update-channel";

    protected final SimpleEconomyManager manager;

    protected final StatefulRedisPubSubConnection<String, String> pubSubConnection;
    protected final StatefulRedisConnection<String, String> statefulRedisConnection;

    protected final Map<String, PlayerData> playersCache = new ConcurrentHashMap<>(Bukkit.getMaxPlayers());
    protected final ScheduledCache<String, PlayerData> temporalPlayersCache = ScheduledCache.<String, PlayerData>builder()
            .setExpireTime(Duration.ofMinutes(10L))
            .build();

    public AbstractStorage(SimpleEconomyManager manager, ConfigFile configFile) {
        this.manager = manager;
        this.pubSubConnection = configFile.getRedisPool().getRedisClient().connectPubSub();
        this.statefulRedisConnection = configFile.getRedisPool().getRedisClient().connect();
        this.pubSubConnection.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                if (!channel.equalsIgnoreCase(SCONOMY_UPDATE_CHANNEL)) return;
                PubSubUpdateMessage updateMessage = PubSubUpdateMessage.fromJson(message);
                if (updateMessage.getServerUUID() == SERVER_UUID) return;
                String playerName = updateMessage.getPlayerName();
                PlayerData playerData = AbstractStorage.this.getCachedPlayerData(playerName);
                if (playerData == null) return;
                Player player = Bukkit.getPlayer(playerName);
                String commentMiniMessage = updateMessage.getCommentMiniMessage();
                if (player != null && commentMiniMessage != null) {
                    player.sendMessage(Message.MINI_MESSAGE.deserialize(commentMiniMessage));
                }
                switch (updateMessage.getMessageType()) {
                    case DEPOSIT_MONEY -> playerData.depositMoney(updateMessage.getCount());
                    case WITHDRAW_MONEY -> playerData.withdrawMoney(updateMessage.getCount());
                    case SET_MONEY -> playerData.setMoney(updateMessage.getCount());
                    case DEPOSIT_COINS -> playerData.depositCoins(updateMessage.getCount());
                    case WITHDRAW_COINS -> playerData.withdrawCoins(updateMessage.getCount());
                    case SET_COINS -> playerData.setCoins(updateMessage.getCount());
                }
            }
        });
    }

    public void publishPubSubUpdateMessage(String playerName, MessageType messageType, int amount, String commentMiniMessage) {
        try {
            this.statefulRedisConnection.async().publish(SCONOMY_UPDATE_CHANNEL, new PubSubUpdateMessage(playerName, messageType, amount, SERVER_UUID, commentMiniMessage).toJson());
        } catch (Exception e) {
            throw new RuntimeException("Error while publish message", e);
        }
    }

    public abstract CompletableFuture<PlayerData> loadPlayerAsync(String playerName);
    public abstract void cache(String nickName);
    public abstract void unCache(String nickName);

    //================VAULT================\\
    public abstract int getMoneyBalance(String nickName);
    public abstract boolean hasMoney(String nickName, int amount);
    public abstract boolean withdrawMoney(String nickName, int amount);
    public abstract boolean depositMoney(String nickName, int amount);

    //================COINS================\\
    public abstract int getCoinsBalance(String nickName);
    //================COINS================\\

    public abstract void clearBalances(String nickName);

    public PlayerData getCachedPlayerData(String nickName) {
        String nickNameLC = nickName.toLowerCase(Locale.ROOT);
        PlayerData playerData = this.playersCache.get(nickNameLC);
        if (playerData == null) {
            playerData = this.temporalPlayersCache.getIfPresent(nickNameLC);
        }
        return playerData;
    }

    @Override
    public abstract void close();
}
