package dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class CreateBookingRequest {

    private final int clientId;
    private final LocalDateTime dateTime;

    public CreateBookingRequest(int clientId, LocalDateTime dateTime) {
        this.clientId = clientId;
        this.dateTime = dateTime;
    }

    public int getClientId() {
        return clientId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDate getVisitDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getVisitTime() {
        return dateTime.toLocalTime();
    }
}