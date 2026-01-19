package service;

import interfaces.ClientRepositoryInterface;
import interfaces.PoolScheduleRepositoryInterface;
import model.Order;
import model.OrderSlot;
import repository.OrderRepositoryJdbc;
import repository.OrderSlotRepositoryJdbc;
import repository.PoolScheduleRepositoryJdbc;
import util.BookingValidator;
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

    public BookingService(OrderRepositoryJdbc orderRepo,
                          OrderSlotRepositoryJdbc slotRepo,
                          PoolScheduleRepositoryInterface poolScheduleRepo,
                          ClientRepositoryInterface clientRepo) {
        this.orderRepo = orderRepo;
        this.slotRepo = slotRepo;
        this.poolScheduleRepo = poolScheduleRepo;
        this.clientRepo = clientRepo;
    }

    public Map<LocalTime, Integer> getBusyStats(LocalDate date) {
        return slotRepo.countBusyByDate(date);
    }

    public int createBooking(int clientId, LocalDate date, LocalTime time) {
        BookingValidator.validateBookingParams(clientId, date, time, poolScheduleRepo);

        if (poolScheduleRepo != null && !poolScheduleRepo.isWorkingHours(date.getDayOfWeek().getValue(), time)) {
            throw new IllegalArgumentException("Time is outside of working hours");
        }

        Map<LocalTime, Integer> currentBookings = slotRepo.countBusyByDate(date);
        int currentCount = currentBookings.getOrDefault(time, 0);
        int maxCapacity = poolScheduleRepo != null ? 
            poolScheduleRepo.getCapacity(date.getDayOfWeek().getValue(), time) : 10;
                
        if (currentCount >= maxCapacity) {
            throw new IllegalStateException("No capacity available for this time slot");
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

    public List<LocalTime> getBusyTimes(LocalDate date) {
        // Validate the date
        BookingValidator.validateDate(date);
        return slotRepo.findBusyTimesByDate(date);
    }

    public Map<LocalTime, Integer> getAvailableStats(LocalDate date) {
        // Validate the date
        BookingValidator.validateDate(date);
        
        // Получаем занятые слоты с количеством
        Map<LocalTime, Integer> busy = slotRepo.countBusyByDate(date);

        Map<LocalTime, Integer> available = new LinkedHashMap<>();
        for (int hour = 0; hour < 24; hour++) {  // Check all hours of the day
            LocalTime t = LocalTime.of(hour, 0);
            
            // Check if this time is within working hours
            if (poolScheduleRepo != null && !poolScheduleRepo.isWorkingHours(date.getDayOfWeek().getValue(), t)) {
                // Skip non-working hours
                continue;
            }
            
            int busyCount = busy.getOrDefault(t, 0);
            int capacity = poolScheduleRepo != null ? 
                poolScheduleRepo.getCapacity(date.getDayOfWeek().getValue(), t) : 10;
            available.put(t, Math.max(0, capacity - busyCount));
        }
        return available;
    }

    public void cancelBooking(int clientId, int orderId) throws SQLException {
        // Validate the parameters
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


}