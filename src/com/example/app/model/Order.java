package model;

import java.time.LocalDate;


public class Order {

    private int id;
    private int clientId;
    private LocalDate visitDate;

    public Order(int id, int clientId, LocalDate visitDate) {
        this.id = id;
        this.clientId = clientId;
        this.visitDate = visitDate;
    }

    public int getId() { return id; }
    public int getClientId() { return clientId; }
    public LocalDate getVisitDate() { return visitDate; }
}
