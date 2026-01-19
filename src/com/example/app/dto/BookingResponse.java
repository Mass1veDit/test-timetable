package dto;



public class BookingResponse {

    private String status;
    private String message;
    private int bookingId;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getBookingId() {
        return bookingId;
    }

    public static BookingResponse success(int id) {
        BookingResponse r = new BookingResponse();
        r.status = "SUCCESS";
        r.bookingId = id;
        return r;
    }
    
    public static BookingResponse successInt(int id) {
        return success(id);
    }

    public static BookingResponse error(String msg) {
        BookingResponse r = new BookingResponse();
        r.status = "ERROR";
        r.message = msg;
        return r;
    }
}

