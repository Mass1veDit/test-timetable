package dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingSearchResult {
    private int orderId;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private LocalDate visitDate;
    private LocalTime visitTime;

    public BookingSearchResult() {}

    public BookingSearchResult(int orderId, String clientName, String clientPhone, 
                              String clientEmail, LocalDate visitDate, LocalTime visitTime) {
        this.orderId = orderId;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public LocalTime getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(LocalTime visitTime) {
        this.visitTime = visitTime;
    }
}