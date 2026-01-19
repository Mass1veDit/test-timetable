package repository;

import interfaces.OrderRepositoryInterface;
import model.Order;
import dto.BookingSearchResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


public class OrderRepositoryJdbc implements OrderRepositoryInterface {

    private final Connection connection;

    public OrderRepositoryJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean existsActiveByClientAndDate(int clientId, LocalDate date) {
        String sql = """
                    SELECT 1 FROM orders
                    WHERE client_id = ? AND visit_date = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.setDate(2, Date.valueOf(date));
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Order create(int clientId, LocalDate date) {
        String sql = """
                    INSERT INTO orders (client_id, visit_date)
                    VALUES (?, ?)
                    RETURNING id
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.setDate(2, Date.valueOf(date));

            ResultSet rs = ps.executeQuery();
            rs.next();
            
            int orderId = rs.getInt(1);
            Order order = new Order(orderId, clientId, date);
            return order;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Order> findAll() throws SQLException {
        String sql = "SELECT id, client_id, visit_date FROM orders ORDER BY visit_date, id";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getDate("visit_date").toLocalDate()
                ));
            }
        }
        return orders;
    }

    // ------------------ Проверка существования заказа ------------------
    public boolean existsById(int orderId) {
        String sql = "SELECT 1 FROM orders WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------ Проверка, принадлежит ли заказ клиенту ------------------
    public boolean isOwner(int clientId, int orderId) {
        String sql = "SELECT 1 FROM orders WHERE id = ? AND client_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, clientId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByOrderId(int orderId) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List<BookingSearchResult> searchBookings(String clientName, LocalDate visitDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT o.id as order_id, c.name as client_name, c.phone as client_phone, 
                   c.email as client_email, o.visit_date, os.visit_time
            FROM orders o
            JOIN clients c ON o.client_id = c.id
            JOIN order_slots os ON o.id = os.order_id
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (clientName != null && !clientName.trim().isEmpty()) {
            sql.append(" AND LOWER(c.name) LIKE LOWER(?)");
            params.add("%" + clientName.trim() + "%");
        }
        
        if (visitDate != null) {
            sql.append(" AND o.visit_date = ?");
            params.add(visitDate);
        }
        
        sql.append(" ORDER BY o.visit_date, os.visit_time, c.name");
        
        List<BookingSearchResult> results = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof LocalDate) {
                    ps.setDate(i + 1, Date.valueOf((LocalDate) params.get(i)));
                } else {
                    ps.setString(i + 1, (String) params.get(i));
                }
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BookingSearchResult result = new BookingSearchResult(
                    rs.getInt("order_id"),
                    rs.getString("client_name"),
                    rs.getString("client_phone"),
                    rs.getString("client_email"),
                    rs.getDate("visit_date").toLocalDate(),
                    rs.getTime("visit_time").toLocalTime()
                );
                results.add(result);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching bookings", e);
        }
        
        return results;
    }
}

