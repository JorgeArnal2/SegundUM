package com.arso.compraventas.event;

import java.time.Instant;

public class DomainEvent {

    private final String type;
    private final Instant occurredOn;

    protected DomainEvent(String type, Instant occurredOn) {
        this.type = type;
        this.occurredOn = occurredOn;
    }

    public String getType() {
        return type;
    }

    public String getEventType() {
        return type;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}
