package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MultiHourBookingRequest {
    private final int clientId;
    private final LocalDate visitDate;
    private final LocalTime startTime;
    private final int hours;

    public MultiHourBookingRequest(int clientId, LocalDate visitDate, LocalTime startTime, int hours) {
        this.clientId = clientId;
        this.visitDate = visitDate;
        this.startTime = startTime;
        this.hours = hours;
    }

    public int getClientId() {
        return clientId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int getHours() {
        return hours;
    }

    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(visitDate, startTime);
    }

    public LocalTime getEndTime() {
        return startTime.plusHours(hours);
    }
}