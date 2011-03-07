package uk.org.lidalia.lang;

import java.util.concurrent.ConcurrentMap;

public class MapUtils {
    public static <K, V> V putIfAbsentReturningValue(ConcurrentMap<K, V> map, K key, V value) {
        V result = map.putIfAbsent(key, value);
        if (result == null) {
            result = value;
        }
        return result;
    }
}
