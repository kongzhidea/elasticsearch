package com.kk.opensearch.model.search.enums;

public enum Ordered {
    DECREASE(0),
    INCREASE(1);

    private int value;

    Ordered(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Ordered getOrder(int value) {
        for (Ordered ordered : Ordered.values()) {
            if (ordered.value == value) {
                return ordered;
            }
        }
        return null;
    }
}
