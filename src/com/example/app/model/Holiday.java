package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Holiday {
    private int id;
    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private int capacity;
    private boolean isClosed;
    
    public Holiday() {}
    
    public Holiday(int id, LocalDate date, LocalTime timeStart, LocalTime timeEnd, int capacity, boolean isClosed) {
        this.id = id;
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.capacity = capacity;
        this.isClosed = isClosed;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalTime getTimeStart() {
        return timeStart;
    }
    
    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }
    
    public LocalTime getTimeEnd() {
        return timeEnd;
    }
    
    public void setTimeEnd(LocalTime timeEnd) {
        this.timeEnd = timeEnd;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public boolean isClosed() {
        return isClosed;
    }
    
    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
