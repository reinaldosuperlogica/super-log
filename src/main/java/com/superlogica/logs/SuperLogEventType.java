package com.superlogica.logs;

public enum SuperLogEventType {
    PERSON_ID("person_id"),
    COMPANY_ID("company_id"),
    EXTERNAL_ID("external_id");

    private String value;

    SuperLogEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
