package repository;

import interfaces.ClientRepositoryInterface;
import model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepositoryJdbc implements ClientRepositoryInterface {

    private final Connection connection;

    public ClientRepositoryJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Client> findAll() {
        String sql = """
            SELECT id, name, phone, email
            FROM clients
            ORDER BY id
        """;

        List<Client> clients = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Client c = new Client();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                clients.add(c);
            }

            return clients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Client findById(int id) {
        String sql = """
            SELECT id, name, phone, email
            FROM clients
            WHERE id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Client c = new Client();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                return c;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Client create(String name, String phone, String email) {
        String sql = """
            INSERT INTO clients (name, phone, email)
            VALUES (?, ?, ?)
            RETURNING id
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);

            ResultSet rs = ps.executeQuery();
            rs.next();

            Client c = new Client();
            c.setId(rs.getInt(1));
            c.setName(name);
            c.setPhone(phone);
            c.setEmail(email);
            return c;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Client update(Client client) {
        String sql = """
            UPDATE clients
            SET name = ?, phone = ?, email = ?
            WHERE id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getPhone());
            ps.setString(3, client.getEmail());
            ps.setInt(4, client.getId());

            ps.executeUpdate();
            return client;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = """
            DELETE FROM clients
            WHERE id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
