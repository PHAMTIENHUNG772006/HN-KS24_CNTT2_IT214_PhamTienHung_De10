package org.example.ticketservice.models.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ticketservice.models.constants.TicketStatus;
import org.example.ticketservice.models.dto.requests.CreateTicketDetailRequest;
import org.example.ticketservice.models.dto.requests.CreateTicketRequest;
import org.example.ticketservice.models.dto.responses.TicketDetailResponse;
import org.example.ticketservice.models.dto.responses.TicketResponse;
import org.example.ticketservice.models.dto.responses.TripResponse;
import org.example.ticketservice.models.entities.Ticket;
import org.example.ticketservice.models.entities.TicketDetail;
import org.example.ticketservice.models.repositories.TicketDetailRepository;
import org.example.ticketservice.models.repositories.TicketRepository;
import org.example.ticketservice.models.services.TicketService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final TicketDetailRepository ticketDetailRepository;
    private final TripGatewayService tripGatewayService;
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Override
    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request){

        List<CreateTicketDetailRequest> detailList = request.items();

        Double totalAmount = 0.0;

        List<TicketDetailResponse>  ticketDetailList = new ArrayList<>();


        for (CreateTicketDetailRequest detail : detailList){

           TripResponse tripResponse =  tripGatewayService.getTripById(detail.tripId());


           if (tripResponse == null){
               throw new NoSuchElementException("Khong tim thay chuyen di theo id : " + tripResponse.id());
           }

           if (detail.seat() < 0 ){
               throw new IllegalArgumentException("Số lượng ghế phải lớn hơn 0");
           }

           if (detail.seat() > tripResponse.availableSeats()){
               throw new IllegalArgumentException("Số lượng ghế không đủ để đặt");
           }


            Ticket ticket = Ticket.builder()
                    .passengerName(request.passengerName())
                    .passengerEmail(request.passengerEmail())
                    .status(TicketStatus.PENDING)
                    .totalAmount(detail.seat() * tripResponse.ticketPrice())
                    .build();

           TicketDetail ticketDetail = TicketDetail.builder()
                   .tripId(tripResponse.id())
                   .ticketPrice(tripResponse.ticketPrice())
                   .seats(detail.seat())
                   .lineTotal(detail.seat() * tripResponse.ticketPrice())
                   .build();

           TicketDetailResponse detailResponse = TicketDetailResponse.builder()
                   .id(ticket.getId())
                   .seats(detail.seat())
                   .ticketPrice(tripResponse.ticketPrice())
                   .lineTotal(detail.seat() * tripResponse.ticketPrice())
                   .build();

           ticketDetailList.add(detailResponse);

           totalAmount += detail.seat()  * tripResponse.ticketPrice();

           ticketRepository.save(ticket);
           ticketDetailRepository.save(ticketDetail);

           kafkaTemplate.send("ticket-created",ticket.getId().toString());
        }


        return TicketResponse.builder()
                .passengerName(request.passengerName())
                .passengerEmail(request.passengerEmail())
                .status(TicketStatus.PENDING)
                .totalAmount(totalAmount)
                .details(ticketDetailList)
                .build();
    }
}
