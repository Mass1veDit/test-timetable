package service;

import interfaces.ClientRepositoryInterface;
import interfaces.PoolScheduleRepositoryInterface;
import interfaces.HolidayRepositoryInterface;
import dto.BookingSearchResult;
import dto.MultiHourBookingRequest;
import model.Order;
import model.OrderSlot;
import model.Holiday;
import model.PoolSchedule;
import repository.OrderRepositoryJdbc;
import repository.OrderSlotRepositoryJdbc;
import repository.PoolScheduleRepositoryJdbc;
import util.BookingValidator;
import util.MultiHourBookingValidator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class BookingService {

    private final OrderRepositoryJdbc orderRepo;
    private final OrderSlotRepositoryJdbc slotRepo;
    private final PoolScheduleRepositoryInterface poolScheduleRepo;
    private final ClientRepositoryInterface clientRepo;
    private final HolidayRepositoryInterface holidayRepo;

    public BookingService(OrderRepositoryJdbc orderRepo,
                          OrderSlotRepositoryJdbc slotRepo,
                          PoolScheduleRepositoryInterface poolScheduleRepo,
                          ClientRepositoryInterface clientRepo,
                          HolidayRepositoryInterface holidayRepo) {
        this.orderRepo = orderRepo;
        this.slotRepo = slotRepo;
        this.poolScheduleRepo = poolScheduleRepo;
        this.clientRepo = clientRepo;
        this.holidayRepo = holidayRepo;
    }

    public Map<LocalTime, Integer> getBusyStats(LocalDate date) {
        return slotRepo.countBusyByDate(date);
    }

    public int createBooking(int clientId, LocalDate date, LocalTime time) {
        BookingValidator.validateBookingParams(clientId, date, time, poolScheduleRepo);

        Holiday holiday = getHolidayForDate(date);
        if (holiday != null) {
            if (holiday.isClosed()) {
                throw new IllegalArgumentException("Pool is closed on this holiday");
            }
            if (!isWithinHolidayHours(holiday, time)) {
                throw new IllegalArgumentException("Time is outside of holiday working hours");
            }
        } else {
            if (poolScheduleRepo != null && !poolScheduleRepo.isWorkingHours(date.getDayOfWeek().getValue(), time)) {
                throw new IllegalArgumentException("Time is outside of working hours");
            }
        }

        int maxCapacity = getMaxCapacityForDateAndTime(date, time, holiday);

        try {
            if (clientRepo.findById(clientId) == null) {
                throw new IllegalArgumentException("Client with ID " + clientId + " does not exist");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking client existence", e);
        }

        try {
            if (orderRepo.existsActiveByClientAndDate(clientId, date)) {
                throw new IllegalStateException("Client already has booking for this day");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        Map<LocalTime, Integer> busy = slotRepo.countBusyByDate(date);

        int currentCount = busy.getOrDefault(time, 0);

        if (currentCount >= maxCapacity) {
            throw new IllegalStateException("No capacity available for this time slot");
        }

        System.out.println("Creating order");
        Order order = null;
        try {
            order = orderRepo.create(clientId, date);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        try {
            slotRepo.save(order.getId(), date, time);
            System.out.println("Slot saved");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return order.getId();
    }
        
    private boolean isWithinPoolSchedule(int dayOfWeek, LocalTime endTime, boolean isHoliday) {
        if (isHoliday) {
            return true;
        } else {
            if (poolScheduleRepo == null) return false;
                
            PoolSchedule schedule = poolScheduleRepo.getByDayOfWeek(dayOfWeek);
            if (schedule == null) return false;

            return !endTime.isAfter(schedule.getTimeEnd());
        }
    }
        
    public List<LocalTime> getBusyTimes(LocalDate date) {
        BookingValidator.validateDate(date);
        return slotRepo.findBusyTimesByDate(date);
    }

    public Map<LocalTime, Integer> getAvailableStats(LocalDate date) {
        BookingValidator.validateDate(date);

        Holiday holiday = getHolidayForDate(date);
        
        // Получаем занятые слоты с количеством
        Map<LocalTime, Integer> busy = slotRepo.countBusyByDate(date);

        Map<LocalTime, Integer> available = new LinkedHashMap<>();
        
        if (holiday != null) {
            if (holiday.isClosed()) {
                return available;
            }

            LocalTime startTime = holiday.getTimeStart() != null ? holiday.getTimeStart() : LocalTime.of(0, 0);
            LocalTime endTime = holiday.getTimeEnd() != null ? holiday.getTimeEnd() : LocalTime.of(23, 59);
            
            for (LocalTime t = startTime; !t.isAfter(endTime); t = t.plusHours(1)) {
                if (t.getMinute() == 0) { // Only full hours
                    int busyCount = busy.getOrDefault(t, 0);
                    int capacity = holiday.getCapacity();
                    available.put(t, Math.max(0, capacity - busyCount));
                }
            }
        } else {
            for (int hour = 0; hour < 24; hour++) {
                LocalTime t = LocalTime.of(hour, 0);

                if (poolScheduleRepo != null && !poolScheduleRepo.isWorkingHours(date.getDayOfWeek().getValue(), t)) {
                    continue;
                }
                
                int busyCount = busy.getOrDefault(t, 0);
                int capacity = poolScheduleRepo != null ? 
                    poolScheduleRepo.getCapacity(date.getDayOfWeek().getValue(), t) : 10;
                available.put(t, Math.max(0, capacity - busyCount));
            }
        }
        
        return available;
    }

    public void cancelBooking(int clientId, int orderId) throws SQLException {
        // Проверка на существование OrderId
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be positive");
        }
        
        // 1. Проверяем, существует ли запись
        if (!orderRepo.existsById(orderId)) {
            throw new IllegalArgumentException("Order not found");
        }

        // 2. Проверяем, что запись принадлежит клиенту
        if (!orderRepo.isOwner(clientId, orderId)) {
            throw new IllegalArgumentException("Order does not belong to this client");
        }

        // 3. Отменяем запись в слоте и статус заказа
        orderRepo.deleteByOrderId(orderId);
    }
    
    public List<BookingSearchResult> searchBookings(String clientName, LocalDate visitDate) throws SQLException {
        return orderRepo.searchBookings(clientName, visitDate);
    }
    
    public int createMultiHourBooking(int clientId, LocalDate date, LocalTime startTime, int hours) {
        MultiHourBookingRequest tempRequest = new MultiHourBookingRequest(clientId, date, startTime, hours);
        MultiHourBookingValidator.validateMultiHourBookingRequest(tempRequest);

        Holiday holiday = getHolidayForDate(date);
        if (holiday != null) {
            if (holiday.isClosed()) {
                throw new IllegalArgumentException("Pool is closed on this holiday");
            }
            if (!isWithinHolidayHours(holiday, startTime)) {
                throw new IllegalArgumentException("Start time is outside of holiday working hours");
            }
            LocalTime endTime = startTime.plusHours(hours);
            if (!isWithinHolidayHours(holiday, endTime)) {
                throw new IllegalArgumentException("End time is outside of holiday working hours");
            }
        } else {
            if (poolScheduleRepo != null && !poolScheduleRepo.isWorkingHours(date.getDayOfWeek().getValue(), startTime)) {
                throw new IllegalArgumentException("Start time is outside of working hours");
            }
            LocalTime endTime = startTime.plusHours(hours);
            if (poolScheduleRepo != null && !isWithinPoolSchedule(date.getDayOfWeek().getValue(), endTime, holiday != null)) {
                throw new IllegalArgumentException("End time is outside of working hours");
            }
        }

        if (!slotRepo.areConsecutiveSlotsAvailable(date, startTime, hours)) {
            throw new IllegalStateException("Some time slots are already booked");
        }

        int maxCapacity = getMaxCapacityForDateAndTime(date, startTime, holiday);

        Map<LocalTime, Integer> busy = slotRepo.countBusyByDate(date);

        for (int i = 0; i < hours; i++) {
            LocalTime currentTime = startTime.plusHours(i);
            int currentCount = busy.getOrDefault(currentTime, 0);

            if (currentCount >= maxCapacity) {
                throw new IllegalStateException(
                    "No capacity available for time slot " + currentTime
                );
            }
        }

        try {
            if (clientRepo.findById(clientId) == null) {
                throw new IllegalArgumentException("Client with ID " + clientId + " does not exist");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking client existence", e);
        }
        
        try {
            if (orderRepo.existsActiveByClientAndDate(clientId, date)) {
                throw new IllegalStateException("Client already has booking for this day");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        System.out.println("Creating multi-hour order");
        Order order = null;
        try {
            order = orderRepo.create(clientId, date);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        try {
            slotRepo.saveMultipleSlots(order.getId(), date, startTime, hours);
            System.out.println("Multiple slots saved");
        } catch (IllegalStateException e) {
            throw new IllegalStateException("One or more time slots became unavailable. Please try again.", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        return order.getId();
    }

    private Holiday getHolidayForDate(LocalDate date) {
        try {
            return holidayRepo.findByDate(date);
        } catch (SQLException e) {
            return null;
        }
    }
    
    private boolean isWithinHolidayHours(Holiday holiday, LocalTime time) {
        if (holiday.getTimeStart() == null || holiday.getTimeEnd() == null) {
            return true;
        }
        return !time.isBefore(holiday.getTimeStart()) && !time.isAfter(holiday.getTimeEnd());
    }
    
    private int getMaxCapacityForDateAndTime(LocalDate date, LocalTime time, Holiday holiday) {
        if (holiday != null) {
            return holiday.getCapacity();
        }
        
        if (poolScheduleRepo != null) {
            return poolScheduleRepo.getCapacity(date.getDayOfWeek().getValue(), time);
        }
        
        return 10;
    }
}