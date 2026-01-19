package repository;

import interfaces.OrderSlotRepositoryInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.Map;
import java.util.LinkedHashMap;

public class OrderSlotRepositoryJdbc implements OrderSlotRepositoryInterface {

    private final Connection connection;

    public OrderSlotRepositoryJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean exists(LocalDate date, LocalTime time) {
        String sql = """
            SELECT 1 FROM order_slots
            WHERE visit_date = ? AND visit_time = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(time));
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(int orderId, LocalDate date, LocalTime time) {
        String sql = """
            INSERT INTO order_slots (order_id, visit_date, visit_time)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(time));

            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("Maximum capacity") || e.getMessage().contains("capacity of")) {
                throw new IllegalStateException("Maximum capacity reached for this time slot", e);
            }
            throw new RuntimeException("Error saving booking", e);
        }
    }

    @Override
    public List<LocalTime> findBusyTimesByDate(LocalDate date) {
        String sql = """
        SELECT visit_time
        FROM order_slots
        WHERE visit_date = ?
        ORDER BY visit_time
    """;

        List<LocalTime> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getTime("visit_time").toLocalTime());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public Map<LocalTime, Integer> countBusyByDate(LocalDate date) {
        String sql = """
            SELECT visit_time, COUNT(*) as cnt
            FROM order_slots
            WHERE visit_date = ?
            GROUP BY visit_time
            ORDER BY visit_time
        """;

        Map<LocalTime, Integer> result = new LinkedHashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LocalTime time = rs.getTime("visit_time").toLocalTime();
                int count = rs.getInt("cnt");
                result.put(time, count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
    
    @Override
    public boolean areConsecutiveSlotsAvailable(LocalDate date, LocalTime startTime, int hours) {
        String sql = """
            SELECT visit_time
            FROM order_slots
            WHERE visit_date = ? AND visit_time >= ? AND visit_time < ?
        """;
        
        LocalTime endTime = startTime.plusHours(hours);
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(startTime));
            ps.setTime(3, Time.valueOf(endTime));
            
            ResultSet rs = ps.executeQuery();
            // If any slots are found, they are occupied
            return !rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking consecutive slot availability", e);
        }
    }

    @Override
    public void saveMultipleSlots(int orderId, LocalDate date, LocalTime startTime, int hours) {
        String sql = """
            INSERT INTO order_slots (order_id, visit_date, visit_time)
            VALUES (?, ?, ?)
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < hours; i++) {
                LocalTime currentTime = startTime.plusHours(i);
                
                ps.setInt(1, orderId);
                ps.setDate(2, Date.valueOf(date));
                ps.setTime(3, Time.valueOf(currentTime));
                ps.addBatch();
            }
            
            ps.executeBatch();
        } catch (SQLException e) {
            if (e.getMessage().contains("Maximum capacity") || e.getMessage().contains("capacity of")) {
                throw new IllegalStateException("Maximum capacity reached for one or more time slots", e);
            }
            throw new RuntimeException("Error saving multiple slots", e);
        }
    }
}


