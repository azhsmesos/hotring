package com.hotRing;

import org.junit.Test;

import com.hotring.HotRingCache;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-01
 */
public class HashTest implements HotRingCache<String, String, String> {

    @Test
    public void testHash() {
        // 13153    194150306
        // 15183    194222279
        int hash = hash("15183");

        System.out.println(hash);
    }


    @Override
    public String search(String key) {
        return null;
    }

    @Override
    public boolean insert(String key, String val) {
        return false;
    }

    @Override
    public boolean update(String key, String val) {
        return false;
    }

    @Override
    public boolean delete(String key) {
        return false;
    }
}
