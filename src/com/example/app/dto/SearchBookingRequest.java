package dto;

public class SearchBookingRequest {
    private String clientName;
    private String visitDate;

    public SearchBookingRequest() {}

    public SearchBookingRequest(String clientName, String visitDate) {
        this.clientName = clientName;
        this.visitDate = visitDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}