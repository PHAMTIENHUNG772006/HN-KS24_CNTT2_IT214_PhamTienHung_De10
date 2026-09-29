package org.example.ticketservice.models.services.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.example.ticketservice.clients.TripClient;
import org.example.ticketservice.models.dto.responses.TripResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripGatewayService {
    private final TripClient tripClient;


    @CircuitBreaker(name = "getTripById", fallbackMethod = "fallBackNotFoundTrip")
    public TripResponse getTripById(Long tripId) {

         TripResponse tripResponse = tripClient.getTripById(tripId);

         if (tripResponse == null) {
             throw new IllegalArgumentException("Chuyến di khong ton tai");
         }
         return tripResponse;
    }

    private TripResponse fallBackNotFoundTrip(Long tripId) {

        TripResponse tripResponse = new TripResponse(tripId,null,"Chuyến bay xyz",null,null);

        return tripResponse;
    }
}
