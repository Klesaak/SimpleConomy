package ua.klesaak.simpleconomy.utils.cache;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <F, S> Pair<F, S> of(F key, S value) {
        return new Pair<>(key, value);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>)o;
        return Objects.equals(this.key, pair.key) && Objects.equals(this.value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }
}
