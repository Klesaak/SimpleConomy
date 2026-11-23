package net.klesaak.simpleconomy.storage.mesenger;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import net.klesaak.storagelib.api.jackson.JacksonAPI;

import java.util.UUID;

@Getter
public class PubSubUpdateMessage {
    private final String playerName;
    private final MessageType messageType;
    private final int count;
    private final UUID serverUUID;
    private final String commentMiniMessage;

    @JsonCreator
    public PubSubUpdateMessage(String playerName, MessageType messageType, int count, UUID serverUUID, String commentMiniMessage) {
        this.playerName = playerName;
        this.messageType = messageType;
        this.count = count;
        this.serverUUID = serverUUID;
        this.commentMiniMessage = commentMiniMessage;
    }

    public String toJson() {
        return JacksonAPI.readAsString(this);
    }

    public static PubSubUpdateMessage fromJson(String data) {
        return JacksonAPI.readValue(data, PubSubUpdateMessage.class);
    }
}
