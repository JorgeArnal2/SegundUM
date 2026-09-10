package com.arso.usuarios.event;

public abstract class DomainEvent {

    private final String type;
    private final String occurredOn;

    protected DomainEvent(String type, String occurredOn) {
        this.type = type;
        this.occurredOn = occurredOn;
    }

    public String getType() {
        return type;
    }

    public String getOccurredOn() {
        return occurredOn;
    }
}
