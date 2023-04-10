package ua.klesaak.simpleconomy.storage.redis;

import ua.klesaak.simpleconomy.manager.PlayerData;
import ua.klesaak.simpleconomy.storage.IStorage;

public class RedisStorage implements IStorage {


    @Override
    public void init() {
    }

    @Override
    public void savePlayer(String nickName, PlayerData playerData) {

    }

    @Override
    public PlayerData getPlayer(String nickName) {
        return null;
    }
}
