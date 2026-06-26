package ua.klesaak.simpleconomy.storage.redis.messenger;

import lombok.Getter;
import lombok.Setter;
import ua.klesaak.simpleconomy.utils.JsonData;

import java.util.UUID;

@Getter @Setter
public class MessageData {
    private String playerName;
    private double amount;

    @Setter
    private UUID uuid;

    public MessageData(String playerName, double amount) {
        this.playerName = playerName;
        this.amount = amount;
    }

    public String toJson() {
        return JsonData.GSON.toJson(this);
    }

    public static MessageData fromJson(String data) {
        return JsonData.GSON.fromJson(data, MessageData.class);
    }
}
