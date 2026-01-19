package service;

import interfaces.ClientRepositoryInterface;
import model.Client;
import util.ClientValidator;

import java.sql.SQLException;
import java.util.List;

public class ClientService {
    private final ClientRepositoryInterface repository;

    public ClientService(ClientRepositoryInterface repository) {
        this.repository = repository;
    }

    public List<Client> getAllClients() throws SQLException {
        return repository.findAll();
    }

    public Client getClientById(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Invalid client id");
        return repository.findById(id);
    }

    public int createClient(String name, String phone, String email) throws SQLException {
        ClientValidator.validateClientFields(name, phone, email);
        
        Client client = repository.create(name, phone, email);
        return client.getId();
    }

    public Client updateClient(int id, String name, String phone, String email) throws SQLException {
        ClientValidator.validateClientId(id);
        ClientValidator.validateClientFieldsForUpdate(name, phone, email);
        
        Client client = repository.findById(id);
        if (client == null) throw new IllegalArgumentException("Invalid client id");
        client.setName(name);
        client.setPhone(phone);
        client.setEmail(email);
        return repository.update(client);
    }
}
