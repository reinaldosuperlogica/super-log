package com.superlogica.logs;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SuperLogLevel {
    WARNING("warning"),
    INFO("info"),
	CRITICAL("critical");

    private String value;

    SuperLogLevel(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
