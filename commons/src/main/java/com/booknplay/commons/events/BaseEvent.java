package com.booknplay.commons.events;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseEvent implements DomainEvent {
    private final UUID id = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();

    @Override
    public UUID id() { return id; }

    @Override
    public Instant occurredAt() { return occurredAt; }
}
