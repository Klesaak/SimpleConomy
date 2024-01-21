package ua.klesaak.simpleconomy.utils.cache;

import lombok.SneakyThrows;
import lombok.val;
import ua.klesaak.mineperms.manager.log.MPLogger;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;


/**
 *
 * @author Klesaak
 * @version 0.0.1
 *
 * Асинхронный кеш, который при надобности очищает устаревшие ключи шедулером.
 * Работает в тестовом режиме, юзать на свой страх и риск.
 */
public class ScheduledCache<K, V> implements AutoCloseable {
    private final Map<K, Pair<Long, V>> data = new ConcurrentHashMap<>();
    private final BiConsumer<K, V> afterRemoving;
    private final BiPredicate<K, V> cancelRemovingIf;
    private final long expireTime;
    private final Executor executor;
    private ScheduledExecutorService executorService;

    public ScheduledCache(Builder<K, V> builder) {
        this.afterRemoving = builder.afterRemoving;
        this.cancelRemovingIf = builder.cancelRemovingIf;
        this.expireTime = builder.expireTime;
        this.executor = builder.executor;
        long clearExpiredInterval = builder.clearExpiredInterval;
        if (clearExpiredInterval > 0L) {
            this.executorService = new ScheduledThreadPoolExecutor(2, runnable -> {
                Thread schedulerThread = new Thread(runnable, "ScheduledCache Thread");
                schedulerThread.setDaemon(false);
                return schedulerThread;
            });
            this.executorService.scheduleWithFixedDelay(this::invalidateData, clearExpiredInterval, clearExpiredInterval, TimeUnit.MILLISECONDS);
        }
    }

    public void clear() {
        this.executor.execute(()-> this.data.keySet().forEach(this::invalidate));
    }

    public void invalidate(K key) {
        val data = this.data.remove(key);
        if (data != null) this.afterRemoving.accept(key, data.getValue());
    }

    public void put(K key, V value) {
        this.executor.execute(()-> {
            this.invalidateData();
            this.data.put(key, Pair.of(System.nanoTime() + this.expireTime, value));
        });
    }

    public void putIfAbsent(K key, V value) {
        this.executor.execute(() -> {
            this.invalidateData();
            this.data.putIfAbsent(key, Pair.of(System.nanoTime() + this.expireTime, value));
        });
    }

    @SneakyThrows({InterruptedException.class, ExecutionException.class})
    public boolean containsKey(K key) {
        return CompletableFuture.runAsync(this::invalidateData, this.executor).thenApply(unused -> this.getIfPresent(key) != null).get();
    }

    public V getIfPresent(K key) {
        val data = this.data.get(key);
        if (data != null && data.getKey() > System.nanoTime()) return data.getValue();
        return null;
    }

    private void invalidateData() {
        for (Map.Entry<K, Pair<Long, V>> obj : this.data.entrySet()) {
            K key = obj.getKey();
            V value = obj.getValue().getValue();
            long expireTime = obj.getValue().getKey();
            if (expireTime < System.nanoTime() && !this.cancelRemovingIf.test(key, value)) {
                this.data.remove(key);
                this.afterRemoving.accept(key, value);
            }
        }
    }

    public int size() {
        return this.data.size();
    }

    public ConcurrentHashMap<K, V> asMap() {
        val map = new ConcurrentHashMap<K, V>(this.data.size());
        for (Map.Entry<K, Pair<Long, V>> obj : this.data.entrySet()) {
            K key = obj.getKey();
            V value = obj.getValue().getValue();
            long expireTime = obj.getValue().getKey();
            if (expireTime > System.nanoTime()) {
                map.put(key, value);
            }
        }
        return map;
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    @Override
    public void close() {
        if (this.executorService == null) return;
        this.executorService.shutdown();
        while (!this.executorService.isTerminated()) {
            try {
                this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static class Builder<K, V> {
        private BiConsumer<K, V> afterRemoving = ((k, v) -> {});
        private BiPredicate<K, V> cancelRemovingIf = ((k, v) -> false);
        private long expireTime = TimeUnit.MINUTES.toMillis(10L);
        private long clearExpiredInterval = 0L;
        private Executor executor = ForkJoinPool.commonPool();

        public Builder() {
        }

        public Builder<K, V> setAfterRemoving(BiConsumer<K, V> afterRemoving) {
            this.afterRemoving = afterRemoving;
            return this;
        }

        public Builder<K, V> setCancelRemovingIf(BiPredicate<K, V> cancelRemovingIf) {
            this.cancelRemovingIf = cancelRemovingIf;
            return this;
        }

        public Builder<K, V> setExpireTime(Duration duration) {
            this.expireTime = duration.toNanos();
            return this;
        }

        public Builder<K, V> clearExpiredInterval(Duration duration) {
            this.clearExpiredInterval = duration.toMillis();
            return this;
        }

        public Builder<K, V> executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public ScheduledCache<K, V> build() {
            return new ScheduledCache<>(this);
        }
    }
}