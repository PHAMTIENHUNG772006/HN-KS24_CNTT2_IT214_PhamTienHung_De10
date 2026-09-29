package org.example.ticketservice.models.dto.responses;

import lombok.Builder;

@Builder
public record TicketDetailResponse(
        Long id,
        Long tripId,
        Integer seats,
        Double ticketPrice,
        Double lineTotal
) {
}
