package com.house.financas.dto;

import java.time.LocalDateTime;

public record GoogleCalendarStatusResponse(
        boolean conectado,
        String calendarId,
        LocalDateTime conectadoEm
) {
}
