package repository;

import interfaces.HolidayRepositoryInterface;
import model.Holiday;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayRepositoryJdbc implements HolidayRepositoryInterface {
    
    private final Connection connection;
    
    public HolidayRepositoryJdbc(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public List<Holiday> findAll() throws SQLException {
        String sql = """
            SELECT id, holiday_date, time_start, time_end, capacity, is_closed
            FROM holidays
            ORDER BY holiday_date
        """;
        
        List<Holiday> holidays = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Holiday holiday = new Holiday();
                holiday.setId(rs.getInt("id"));
                holiday.setDate(rs.getDate("holiday_date").toLocalDate());
                holiday.setTimeStart(rs.getTime("time_start") != null ? 
                    rs.getTime("time_start").toLocalTime() : null);
                holiday.setTimeEnd(rs.getTime("time_end") != null ? 
                    rs.getTime("time_end").toLocalTime() : null);
                holiday.setCapacity(rs.getInt("capacity"));
                holiday.setClosed(rs.getBoolean("is_closed"));
                holidays.add(holiday);
            }
        }
        return holidays;
    }
    
    @Override
    public Holiday findByDate(LocalDate date) throws SQLException {
        String sql = """
            SELECT id, holiday_date, time_start, time_end, capacity, is_closed
            FROM holidays
            WHERE holiday_date = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Holiday holiday = new Holiday();
                holiday.setId(rs.getInt("id"));
                holiday.setDate(rs.getDate("holiday_date").toLocalDate());
                holiday.setTimeStart(rs.getTime("time_start") != null ? 
                    rs.getTime("time_start").toLocalTime() : null);
                holiday.setTimeEnd(rs.getTime("time_end") != null ? 
                    rs.getTime("time_end").toLocalTime() : null);
                holiday.setCapacity(rs.getInt("capacity"));
                holiday.setClosed(rs.getBoolean("is_closed"));
                return holiday;
            }
            return null;
        }
    }
}