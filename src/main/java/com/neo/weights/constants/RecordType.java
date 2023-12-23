package com.neo.weights.constants;

public enum RecordType {

    PROD("Prod"),
    PROD_CURRENT("ProdCurrent"),
    CHANGE_COUNTER("ChangeCounter"),
    CONST_COUNTER("ConstCounter"),
    FT("FT"),
    COUNTER("Counter");
    private final String value;

    RecordType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
