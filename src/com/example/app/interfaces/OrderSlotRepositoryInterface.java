package interfaces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface OrderSlotRepositoryInterface {

    boolean exists(LocalDate date, LocalTime time);

    void save(int orderId, LocalDate date, LocalTime time);

    Map<LocalTime, Integer> countBusyByDate(LocalDate date);

    List<LocalTime> findBusyTimesByDate(LocalDate date);
}
