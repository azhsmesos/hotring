package com.hotring;

import java.util.HashMap;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-01
 */
public class HotHashMap extends HashMap<String, String> {

    // 统计总的查找次数
    private int findCnt;

    // 统计最大查找次数，一定程度上可以反应尾延迟
    private int maxFindCnt;

    // 统计最小查找次数
    private int minFindCnt;

    public HotHashMap(int m) {
        super(m);
    }

    @Override
    public String put(String key, String value) {
        return super.put(key, value);
    }

    @Override
    public String get(Object key) {
        this.findCnt++;
        return super.get(key);
    }

    public void setMinMax(int oneCnt) {
        this.maxFindCnt = Math.max(this.maxFindCnt, oneCnt);
        this.minFindCnt = Math.min(this.minFindCnt, oneCnt);
    }

    public int getFindCnt() {
        return findCnt;
    }

    public void setFindCnt(int findCnt) {
        this.findCnt = findCnt;
    }

    public int getMaxFindCnt() {
        return maxFindCnt;
    }

    public void setMaxFindCnt(int maxFindCnt) {
        this.maxFindCnt = maxFindCnt;
    }

    public int getMinFindCnt() {
        return minFindCnt;
    }

    public void setMinFindCnt(int minFindCnt) {
        this.minFindCnt = minFindCnt;
    }
}
