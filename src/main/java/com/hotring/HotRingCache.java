package com.hotring;


import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-01
 */
public interface HotRingCache<K, V, T> extends BiFunction<K, V, T> {

    T search(K key);

    boolean insert(K key, V val);

    boolean update(K key, V val);

    boolean delete(K key);

    default int hash(K key) {
        if (key instanceof String) {
            int hash = 5381;
            for (char c : ((String) key).toCharArray()) {
                hash += (hash << 5) + c;
            }
            // return key[0] << 7 将最高位设置为0，非负数
            return (hash & 0x7FFFFFFF);
        }
        // 否则取地址
        return System.identityHashCode(key);
    }

    @Override
    default T apply(K k, V v) {
        return null;
    }
}
