package com.superlogica.logs;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SuperLogChannel {
    STDOUT("stdout"),
	BROKER("broker");

    private String value;

    SuperLogChannel(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
