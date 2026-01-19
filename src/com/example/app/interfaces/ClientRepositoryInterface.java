package interfaces;

import model.Client;
import java.sql.SQLException;
import java.util.List;

public interface ClientRepositoryInterface {
    List<Client> findAll() throws SQLException;
    Client findById(int id) throws SQLException;
    Client create(String name, String phone, String email) throws SQLException;
    Client update(Client client) throws SQLException;
    void deleteById(int id) throws SQLException;
}
