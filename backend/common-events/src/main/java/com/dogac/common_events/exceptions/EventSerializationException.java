package com.dogac.common_events.exceptions;

public class EventSerializationException extends RuntimeException {

    public EventSerializationException(Class<?> eventType, Throwable cause) {
        super(eventType.getSimpleName() + " serialization failed", cause);
    }
}
