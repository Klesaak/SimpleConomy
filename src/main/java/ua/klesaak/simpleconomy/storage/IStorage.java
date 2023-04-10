package ua.klesaak.simpleconomy.storage;

import lombok.Synchronized;
import ua.klesaak.simpleconomy.manager.PlayerData;

public interface IStorage {
    void init();
    @Synchronized
    void savePlayer(String nickName, PlayerData playerData);
    PlayerData getPlayer(String nickName);
}
