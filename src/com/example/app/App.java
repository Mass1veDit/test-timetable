import com.sun.net.httpserver.HttpServer;
import controller.ClientController;
import repository.ClientRepositoryJdbc;
import service.ClientService;

import controller.BookingController;
import repository.OrderRepositoryJdbc;
import repository.OrderSlotRepositoryJdbc;
import repository.PoolScheduleRepositoryJdbc;
import repository.HolidayRepositoryJdbc;
import service.BookingService;

import java.net.InetSocketAddress;
import java.sql.Connection;

import db.Db;

public class App {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress("localhost", 8081), 0);

        Connection connection = Db.getConnection();

        ClientRepositoryJdbc repo = new ClientRepositoryJdbc(connection);
        ClientService service = new ClientService(repo);
        ClientController controller = new ClientController(service);

        OrderRepositoryJdbc orderRepo = new OrderRepositoryJdbc(connection);
        OrderSlotRepositoryJdbc orderSlotRepo = new OrderSlotRepositoryJdbc(connection);
        PoolScheduleRepositoryJdbc poolScheduleRepo = new PoolScheduleRepositoryJdbc(connection);
        ClientRepositoryJdbc clientRepo = new ClientRepositoryJdbc(connection);
        HolidayRepositoryJdbc holidayRepo = new HolidayRepositoryJdbc(connection);

        BookingService bookingService = new BookingService(orderRepo, orderSlotRepo, poolScheduleRepo, clientRepo, holidayRepo);
        BookingController bookingController = new BookingController(bookingService);

        // Получить всех клиентов
        server.createContext("/api/v0/pool/client/all", controller);

        // Получить одного клиента по id
        server.createContext("/api/v0/pool/client/get", controller);

        // Добавление нового клиента
        server.createContext("/api/v0/pool/client/add", controller);

        // Обновление клиента
        server.createContext("/api/v0/pool/client/update", controller);

        // Получение занятых записей
        server.createContext("/api/v0/pool/timetable/all", bookingController);

        // Получение доступных записей
        server.createContext("/api/v0/pool/timetable/available", bookingController);

        // Бронирование
        server.createContext("/api/v0/pool/timetable/reserve", bookingController);

        // уДаление брони
        server.createContext("/api/v0/pool/timetable/cancel", bookingController);
        
        // Поиск записей
        server.createContext("/api/v0/pool/timetable/search", bookingController);
        
        // Многочасовое бронирование
        server.createContext("/api/v0/pool/timetable/reserve/multi", bookingController);

        server.start();

        System.out.println("Server started http://localhost:8081/clients");
    }
}
