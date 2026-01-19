-- Pool Schedule Table
CREATE TABLE IF NOT EXISTS pool_schedules (
    id SERIAL PRIMARY KEY,
    day_of_week INTEGER NOT NULL, -- 1=Monday, 2=Tuesday, ..., 7=Sunday
    time_start TIME NOT NULL,
    time_end TIME NOT NULL,
    capacity INTEGER DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE pool_schedules
ADD CONSTRAINT uq_pool_day UNIQUE(day_of_week);

-- Insert default working hours (8 AM to 10 PM) with capacity of 10 for all days
INSERT INTO pool_schedules (day_of_week, time_start, time_end, capacity)
SELECT day, '08:00:00'::TIME, '22:00:00'::TIME, 10
FROM generate_series(1, 7) AS day
ON CONFLICT (day_of_week) DO NOTHING;

-- Clients Table
CREATE TABLE IF NOT EXISTS clients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL,
    visit_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- Order Slots Table - Main table for bookings
CREATE TABLE IF NOT EXISTS order_slots (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    visit_date DATE NOT NULL,
    visit_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Holidays Table
CREATE TABLE IF NOT EXISTS holidays (
    id SERIAL PRIMARY KEY,
    holiday_date DATE NOT NULL UNIQUE,
    time_start TIME,
    time_end TIME,
    capacity INTEGER DEFAULT 10,
    is_closed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_order_slots_date_time ON order_slots(visit_date, visit_time);
CREATE INDEX IF NOT EXISTS idx_orders_client_date ON orders(client_id, visit_date);
CREATE INDEX IF NOT EXISTS idx_pool_schedules_day ON pool_schedules(day_of_week);
CREATE INDEX IF NOT EXISTS idx_holidays_date ON holidays(holiday_date);