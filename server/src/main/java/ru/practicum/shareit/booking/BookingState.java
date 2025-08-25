package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.ValidationException;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState from(String raw) {
        try {
            return raw == null ? ALL : BookingState.valueOf(raw.toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + raw);
        }
    }
}
