package dev.fadest.pets.util;

import lombok.Data;

@Data
public class Pair<K, T> {

    private final K key;
    private final T value;

    public static <K, T> Pair<K, T> of(K key, T value) {
        return new Pair<>(key, value);
    }
}