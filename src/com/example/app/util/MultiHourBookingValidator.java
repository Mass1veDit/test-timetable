package util;

import dto.MultiHourBookingRequest;

public class MultiHourBookingValidator {
    
    public static void validateMultiHourBookingRequest(MultiHourBookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        
        if (!ValidationUtil.isValidClientId(request.getClientId())) {
            throw new IllegalArgumentException("Invalid client ID: must be a positive integer");
        }
        
        if (!ValidationUtil.isValidFutureDate(request.getVisitDate())) {
            throw new IllegalArgumentException("Invalid date: must not be in the past");
        }
        
        if (!ValidationUtil.isValidPoolTime(request.getStartTime())) {
            throw new IllegalArgumentException("Invalid start time: pool works from 08:00 to 22:00 and only full-hour slots allowed");
        }
        
        if (!ValidationUtil.isValidHours(request.getHours())) {
            throw new IllegalArgumentException("Invalid hours: must be between 1 and 12");
        }
        
        if (!ValidationUtil.areConsecutiveHoursValid(request.getStartTime(), request.getHours())) {
            throw new IllegalArgumentException("Booking would extend beyond pool working hours");
        }
    }
}