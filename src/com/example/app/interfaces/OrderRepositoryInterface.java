package interfaces;
import model.Order;
import dto.BookingSearchResult;
import java.time.LocalDate;
import java.util.List;


public interface OrderRepositoryInterface {

    boolean existsActiveByClientAndDate(int clientId, LocalDate date);

    Order create(int clientId, LocalDate date);

    void deleteByOrderId(int orderId);
    
    List<BookingSearchResult> searchBookings(String clientName, LocalDate visitDate);
}