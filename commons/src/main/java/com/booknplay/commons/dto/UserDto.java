package com.booknplay.commons.dto;

// Simple Java record replacing Lombok @Value/@Builder to avoid annotation processing issues.
public record UserDto(String id, String email, String role) {}
