package ua.klesaak.simpleconomy.storage.mysql;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author _Shevchik_
 */

public class AsyncUpdateContainer<T> {
    private final Dao<T, ?> dao;
    private final ScheduledExecutorService executor;
    private final Object updateLock = new Object();
    private volatile boolean updateScheduled = false;
    private final T object;


    /**
     * @param object объект-класс, с которым будем оперировать, например: "PlayerData::new"
     */
    public AsyncUpdateContainer(Dao<T, ?> dao, ScheduledExecutorService executor, T object) {
        this.dao = dao;
        this.executor = executor;
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void scheduleUpdate() {
        synchronized (updateLock) {
            if (updateScheduled) {
                return;
            }
            updateScheduled = true;
        }
        Runnable run = () -> {
            synchronized (updateLock) {
                updateScheduled = false;
            }
            try {
                dao.createOrUpdate(object);
            } catch (SQLException e) {
                System.out.println("Unable to save sql data: " + e);
            }
        };
        executor.execute(run);
    }
}
