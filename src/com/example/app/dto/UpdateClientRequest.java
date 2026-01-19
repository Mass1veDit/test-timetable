package dto;

public class UpdateClientRequest {
    private final int id;
    private final String name;
    private final String phone;
    private final String email;

    public UpdateClientRequest(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}
