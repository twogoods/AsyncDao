package com.tg.async.mysql;

/**
 * Created by twogoods on 2018/4/12.
 */
public enum  TransactionIsolation {
    READ_UNCOMMITTED(1),
    READ_COMMITTED(2),
    REPEATABLE_READ(4),
    SERIALIZABLE(8),
    NONE(0);

    private final int type;

    private TransactionIsolation(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
