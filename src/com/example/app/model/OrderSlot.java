package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class OrderSlot {

    private int id;
    private LocalDate date;
    private LocalTime time;

    public OrderSlot(LocalDate date, LocalTime time) {
        if (time.getMinute() != 0) {
            throw new IllegalArgumentException("Slot must start at full hour");
        }
        this.date = date;
        this.time = time;
    }

    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
}
