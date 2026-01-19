package repository;

import interfaces.PoolScheduleRepositoryInterface;
import model.PoolSchedule;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PoolScheduleRepositoryJdbc implements PoolScheduleRepositoryInterface {

    private final Connection connection;

    public PoolScheduleRepositoryJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public PoolSchedule getByDayOfWeek(int dayOfWeek) {
        String sql = """
            SELECT id, day_of_week, time_start, time_end, capacity
            FROM pool_schedules
            WHERE day_of_week = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dayOfWeek);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new PoolSchedule(
                    rs.getInt("id"),
                    rs.getInt("day_of_week"),
                    rs.getTime("time_start").toLocalTime(),
                    rs.getTime("time_end").toLocalTime(),
                    rs.getInt("capacity")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving pool schedule", e);
        }

        return null;
    }

    @Override
    public List<PoolSchedule> getAll() {
        String sql = """
            SELECT id, day_of_week, time_start, time_end, capacity
            FROM pool_schedules
            ORDER BY day_of_week
            """;

        List<PoolSchedule> result = new ArrayList<>();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                result.add(new PoolSchedule(
                    rs.getInt("id"),
                    rs.getInt("day_of_week"),
                    rs.getTime("time_start").toLocalTime(),
                    rs.getTime("time_end").toLocalTime(),
                    rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all pool schedules", e);
        }

        return result;
    }

    @Override
    public boolean isWorkingHours(int dayOfWeek, LocalTime time) {
        PoolSchedule schedule = getByDayOfWeek(dayOfWeek);
        if (schedule == null) {
            // If no schedule is defined for the day, assume closed
            return false;
        }

        // Check if the time falls within the working hours
        return !time.isBefore(schedule.getTimeStart()) && time.isBefore(schedule.getTimeEnd());
    }

    @Override
    public int getCapacity(int dayOfWeek, LocalTime time) {
        PoolSchedule schedule = getByDayOfWeek(dayOfWeek);
        if (schedule == null) {
            // Default capacity if no schedule is defined
            return 10;
        }

        return schedule.getCapacity();
    }
}