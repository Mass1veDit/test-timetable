package interfaces;

import model.PoolSchedule;
import java.time.LocalTime;
import java.util.List;

public interface PoolScheduleRepositoryInterface {
    
    /**
     * Gets the pool schedule for a specific day of the week
     */
    PoolSchedule getByDayOfWeek(int dayOfWeek);
    
    /**
     * Gets all pool schedules
     */
    List<PoolSchedule> getAll();
    
    /**
     * Checks if the given time is within working hours for the specified day
     */
    boolean isWorkingHours(int dayOfWeek, LocalTime time);
    
    /**
     * Gets the capacity for a specific day and time
     */
    int getCapacity(int dayOfWeek, LocalTime time);
}