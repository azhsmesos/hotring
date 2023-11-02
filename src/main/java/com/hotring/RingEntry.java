package com.hotring;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-10-31
 * 随机移动策略对象，后期会删除
 */
public class RingEntry implements Comparable<RingEntry> {

    private String key; // 键
    private String val; // 值
    private int tag; // tag值
    private byte occupied; // 占用标识，多线程实现时启用
    private byte rehash; // rehash标识
    private RingEntry next; // 指向下个哈希表节点，形成链表

    public RingEntry(String key, String val, RingEntry next, int tag, byte occupied, byte rehash) {
        this.key = key;
        this.val = val;
        this.next = next;
        this.tag = tag;
        this.occupied = occupied;
        this.rehash = rehash;
    }

    public RingEntry() {
        this("", "", null, 0, (byte) 0, (byte) 0);
    }

    public RingEntry(String key, String val, RingEntry next, int tag) {
        this(key, val, next, tag, (byte) 0, (byte) 0);
    }

    public RingEntry(String key, int tag) {
        this(key, "", null, tag, (byte) 0, (byte) 0);
    }

    /**
     * 如果 < 就返回 -1
     * 如果 == 就返回 0
     * 如果 > 就返回 1
     * @param o the object to be compared.
     * @return -1 0 1
     */
    @Override
    public int compareTo(RingEntry o) {
        if (this.tag == o.getTag()) {
            return this.key.compareTo(o.getKey());
        }
        return Integer.compare(this.tag, o.getTag());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public byte getOccupied() {
        return occupied;
    }

    public void setOccupied(byte occupied) {
        this.occupied = occupied;
    }

    public byte getRehash() {
        return rehash;
    }

    public void setRehash(byte rehash) {
        this.rehash = rehash;
    }

    public RingEntry getNext() {
        return next;
    }

    public void setNext(RingEntry next) {
        this.next = next;
    }
}
