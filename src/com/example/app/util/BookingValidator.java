package util;

import dto.CreateBookingRequest;
import interfaces.PoolScheduleRepositoryInterface;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingValidator {
    public static void validateCreateBookingRequest(CreateBookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (!ValidationUtil.isValidClientId(request.getClientId())) {
            throw new IllegalArgumentException("Invalid client ID: must be a positive integer");
        }

        LocalDate visitDate = request.getVisitDate();
        if (!ValidationUtil.isValidFutureDate(visitDate)) {
            throw new IllegalArgumentException("Invalid date: must not be in the past");
        }

        LocalTime visitTime = request.getVisitTime();
        if (!ValidationUtil.isValidPoolTime(visitTime)) {
            throw new IllegalArgumentException("Invalid time: pool works from 08:00 to 22:00 and only full-hour slots allowed");
        }
    }
    
    public static void validateCreateBookingRequest(CreateBookingRequest request, PoolScheduleRepositoryInterface poolScheduleRepo) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (!ValidationUtil.isValidClientId(request.getClientId())) {
            throw new IllegalArgumentException("Invalid client ID: must be a positive integer");
        }

        LocalDate visitDate = request.getVisitDate();
        if (!ValidationUtil.isValidFutureDate(visitDate)) {
            throw new IllegalArgumentException("Invalid date: must not be in the past");
        }

        LocalTime visitTime = request.getVisitTime();
        validateTimeAgainstSchedule(visitDate.getDayOfWeek().getValue(), visitTime, poolScheduleRepo);
    }
    
    private static void validateTimeAgainstSchedule(int dayOfWeek, LocalTime time, PoolScheduleRepositoryInterface poolScheduleRepo) {
        if (poolScheduleRepo != null) {
            if (!poolScheduleRepo.isWorkingHours(dayOfWeek, time)) {
                throw new IllegalArgumentException("Time is outside of working hours");
            }
        } else {
            // Fallback to default validation if no schedule repository is provided
            if (!ValidationUtil.isValidPoolTime(time)) {
                throw new IllegalArgumentException("Invalid time: pool works from 08:00 to 22:00 and only full-hour slots allowed");
            }
        }
    }

    public static void validateBookingParams(int clientId, LocalDate date, LocalTime time) {
        if (!ValidationUtil.isValidClientId(clientId)) {
            throw new IllegalArgumentException("Invalid client ID: must be a positive integer");
        }

        if (!ValidationUtil.isValidFutureDate(date)) {
            throw new IllegalArgumentException("Invalid date: must not be in the past");
        }

        if (!ValidationUtil.isValidPoolTime(time)) {
            throw new IllegalArgumentException("Invalid time: pool works from 08:00 to 22:00 and only full-hour slots allowed");
        }
    }
    
    public static void validateBookingParams(int clientId, LocalDate date, LocalTime time, PoolScheduleRepositoryInterface poolScheduleRepo) {
        if (!ValidationUtil.isValidClientId(clientId)) {
            throw new IllegalArgumentException("Invalid client ID: must be a positive integer");
        }

        if (!ValidationUtil.isValidFutureDate(date)) {
            throw new IllegalArgumentException("Invalid date: must not be in the past");
        }

        validateTimeAgainstSchedule(date.getDayOfWeek().getValue(), time, poolScheduleRepo);
    }

    public static void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
    }

    public static void validateOrderId(String orderId) {
        if (!ValidationUtil.isValidUuid(orderId)) {
            throw new IllegalArgumentException("Invalid order ID format");
        }
    }

    public static void validateCancellationParams(int clientId, String orderId) {
        if (!ValidationUtil.isValidClientId(clientId)) {
            throw new IllegalArgumentException("Invalid client ID: must be a positive integer");
        }

        if (!ValidationUtil.isValidUuid(orderId)) {
            throw new IllegalArgumentException("Invalid order ID format");
        }
    }
}