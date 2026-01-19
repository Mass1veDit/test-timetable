package interfaces;
import model.Order;
import java.time.LocalDate;


public interface OrderRepositoryInterface {

    boolean existsActiveByClientAndDate(int clientId, LocalDate date);

    Order create(int clientId, LocalDate date);

    void deleteByOrderId(int orderId);
}