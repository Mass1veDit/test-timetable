1. Компиляция проекта:

```javac -d out (Get-ChildItem src -Recurse -Filter *.java).FullName```

2. Запуск проекта:

```java -cp "out;lib/postgresql-42.6.0.jar" App```

Поиск записи по Фио, Дате посещения

GET http://localhost:8081/api/v0/pool/timetable/search

1. Поиск по двум параметрам

```
{
    "clientName": "test",
    "visitDate": "2024-01-15"
}
```

1. Поиск по дате посещения параметрам

```
{
  "clientName": "",
  "visitDate": "2024-01-15"
}

```

1. Поиск по Имени

```

{
  "clientName": "test",
  "visitDate": ""
}
```

Запись на несколько часов вперед
POST http://localhost:8081/api/v0/pool/timetable/reserve/multi
```
{
    "clientId": 123,
    "date": "2024-01-15",
    "startTime": "10:00",
    "hours": 3
}
```

Праздничные дни: holidays

#### Расписание:
Добавление записи

POST http://localhost:8081/api/v0/pool/timetable/reserve
```
{
    "clientId": 2,
    "datetime": "2026-04-05T10:00"
}
```

Удаление записи

DELETE http://localhost:8081/api/v0/pool/timetable/cancel
```
{
    "clientId": 2,
    "datetime": "2026-04-05T10:00"
}
```

Получение занятых записей
GET http://localhost:8081/api/v0/pool/timetable/all
```
{
    "date": "2029-03-20"
}
```

Показ доступных записей
GET http://localhost:8081/api/v0/pool/timetable/available
```
{
    "date": "2029-03-20"
}
```

#### Клиенты: 
Добавление пользователя
POST http://localhost:8081/api/v0/pool/client/add
```
{
    "name": "dasdasdasdasd",
    "phone": 7960435345,
    "email": "sadasd@mail.ru"
}
```

Показ всех пользователей
GET http://localhost:8081/api/v0/pool/client/all

Показ клиента по ID
GET http://localhost:8081/api/v0/pool/client/get
```
{
    "id": 1
}
```

Обновления информации клиента
POST http://localhost:8081/api/v0/pool/client/update
```
{
    "id": 1,
    "name": "asdasd",
    "phone": "54754765465",
    "email": "asdasd@asdmil.ru"
}
```