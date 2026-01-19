package interfaces;

import model.Holiday;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface HolidayRepositoryInterface {
    List<Holiday> findAll() throws SQLException;
    Holiday findByDate(LocalDate date) throws SQLException;
}