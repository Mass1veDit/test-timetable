package dto;

public class ClientResponse {
    private int id;

    public int getId() {
        return id;
    }

    // Статический метод для успешного ответа
    public static ClientResponse success(int id) {
        ClientResponse r = new ClientResponse();
        r.id = id;
        return r;
    }
}
