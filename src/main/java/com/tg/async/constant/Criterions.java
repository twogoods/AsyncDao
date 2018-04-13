package com.tg.async.constant;

/**
 * Created by twogoods on 2018/4/12.
 */
public enum Criterions {
    EQUAL("="),
    NOT_EQUAL("!="),
    GREATER(">"),
    GREATER_OR_EQUAL(">="),
    LESS("<="),
    LESS_OR_EQUAL("<="),
    LIKE("like"),
    IN("in"),
    NOT_IN("not in");


    private String criterion;

    Criterions(String criterion) {
        this.criterion = criterion;
    }

    public String getCriterion() {
        return criterion;
    }

    public boolean inCriterion() {
        if ("in".equals(criterion) || "not in".equals(criterion)) {
            return true;
        }
        return false;
    }
}
