package com.solviads.cmis.constants;

public enum QueryEnum {
    SELECT_DOCUMENTS("SELECT * FROM cmis:document");
    private final String value;

    QueryEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
