package org.example.ticketservice.models.dto.responses;

import lombok.Builder;
import org.example.ticketservice.models.constants.TicketStatus;

import java.util.List;

@Builder
public record TicketResponse(
        Long id,
        String passengerName,
        String passengerEmail,
        Double totalAmount,
        TicketStatus status,
        List<TicketDetailResponse> details
) {
}
