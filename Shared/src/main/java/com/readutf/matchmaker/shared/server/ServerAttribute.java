package com.readutf.matchmaker.shared.server;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @Getter
public class ServerAttribute {

    private String encoded;
    private Class<?> type;

    /**
     * Represents a server attribute
     * @param encoded Encoded base64 of the attribute
     * @param type Type of the attribute
     */
    public ServerAttribute(String encoded, Class<?> type) {
        this.encoded = encoded;
        this.type = type;
    }
}
