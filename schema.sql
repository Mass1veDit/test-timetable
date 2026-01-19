-- =====================================================
-- Таблица расписания работы бассейна
-- =====================================================
-- Хранит рабочие часы и вместимость по дням недели
-- day_of_week: 1 = понедельник, 7 = воскресенье
CREATE TABLE IF NOT EXISTS pool_schedules (
    id SERIAL PRIMARY KEY,
    day_of_week INTEGER NOT NULL,      -- День недели (1–7)
    time_start TIME NOT NULL,          -- Время открытия
    time_end TIME NOT NULL,            -- Время закрытия
    capacity INTEGER DEFAULT 10,        -- Вместимость на один час
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Один день недели — одна запись
ALTER TABLE pool_schedules
    ADD CONSTRAINT uq_pool_day UNIQUE(day_of_week);

-- Заполняем расписание по умолчанию:
-- каждый день с 08:00 до 22:00, вместимость 10 человек
INSERT INTO pool_schedules (day_of_week, time_start, time_end, capacity)
SELECT day, '08:00:00'::TIME, '22:00:00'::TIME, 10
FROM generate_series(1, 7) AS day
ON CONFLICT (day_of_week) DO NOTHING;

-- =====================================================
-- Таблица клиентов
-- =====================================================
CREATE TABLE IF NOT EXISTS clients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,         -- Имя клиента
    phone VARCHAR(20) NOT NULL,         -- Телефон
    email VARCHAR(255) NOT NULL,        -- Email
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- =====================================================
-- Таблица заказов (одно бронирование в день на клиента)
-- =====================================================
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL,         -- Клиент
    visit_date DATE NOT NULL,           -- Дата посещения
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- =====================================================
-- Таблица временных слотов бронирования
-- =====================================================
-- Каждый слот — это 1 час (ровно HH:00)
CREATE TABLE IF NOT EXISTS order_slots (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,          -- Связь с заказом
    visit_date DATE NOT NULL,           -- Дата посещения
    visit_time TIME NOT NULL,           -- Время начала часа (HH:00)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- =====================================================
-- Таблица праздничных дней
-- =====================================================
CREATE TABLE IF NOT EXISTS holidays (
    id SERIAL PRIMARY KEY,
    holiday_date DATE NOT NULL UNIQUE,  -- Дата праздника
    time_start TIME,                    -- Время открытия (если отличается)
    time_end TIME,                      -- Время закрытия (если отличается)
    capacity INTEGER DEFAULT 10,        -- Вместимость в праздник
    is_closed BOOLEAN DEFAULT FALSE,    -- Полностью закрыт?
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Индексы для ускорения запросов
-- =====================================================

-- Быстрый поиск занятых слотов по дате и времени
CREATE INDEX IF NOT EXISTS idx_order_slots_date_time
    ON order_slots(visit_date, visit_time);

-- Проверка: есть ли у клиента бронь на конкретный день
CREATE INDEX IF NOT EXISTS idx_orders_client_date
    ON orders(client_id, visit_date);

-- Поиск расписания по дню недели
CREATE INDEX IF NOT EXISTS idx_pool_schedules_day
    ON pool_schedules(day_of_week);

-- Поиск праздничных дней
CREATE INDEX IF NOT EXISTS idx_holidays_date
    ON holidays(holiday_date);