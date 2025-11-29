package com.booknplay.commons.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    UUID id();
    Instant occurredAt();
    String type();
}
