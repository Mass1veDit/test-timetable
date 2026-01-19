package model;

import java.time.LocalTime;

public class PoolSchedule {

    private int id;
    private int dayOfWeek;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private int capacity;

    public PoolSchedule(int id, int dayOfWeek, LocalTime timeStart, LocalTime timeEnd) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.capacity = 10; // default capacity
    }
    
    public PoolSchedule(int id, int dayOfWeek, LocalTime timeStart, LocalTime timeEnd, int capacity) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getTimeStart() {
        return timeStart;
    }

    public LocalTime getTimeEnd() {
        return timeEnd;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
