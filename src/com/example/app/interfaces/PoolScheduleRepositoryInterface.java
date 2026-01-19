package interfaces;

import model.PoolSchedule;
import java.time.LocalTime;
import java.util.List;

public interface PoolScheduleRepositoryInterface {

    PoolSchedule getByDayOfWeek(int dayOfWeek);
    List<PoolSchedule> getAll();
    boolean isWorkingHours(int dayOfWeek, LocalTime time);
    int getCapacity(int dayOfWeek, LocalTime time);
}