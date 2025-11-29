package com.booknplay.commons.events;

public final class SchemaConstants {
    public static final String BOOKING_EVENT_V1 = "{\n  \"type\": \"record\",\n  \"name\": \"BookingEvent\",\n  \"fields\": [\n    {\"name\": \"id\", \"type\": \"string\"},\n    {\"name\": \"turfId\", \"type\": \"long\"},\n    {\"name\": \"slotStart\", \"type\": \"string\"}\n  ]\n}";
    private SchemaConstants() {}
}
