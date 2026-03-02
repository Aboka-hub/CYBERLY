INSERT INTO users (email, password, status, created_at)
VALUES
    ('alex@mail.com', '$2a$10$dummyencodedpassword1', 'ACTIVE', NOW()),
    ('nureke@mail.com', '$2a$10$dummyencodedpassword2', 'ACTIVE', NOW()),
    ('blocked@mail.com', '$2a$10$dummyencodedpassword3', 'BLOCKED', NOW());

INSERT INTO subscriptions (user_id, service_name, amount, next_payment_date, active, created_at)
VALUES
    (1, 'Netflix', 12.99, CURRENT_DATE + INTERVAL '5 days', true, NOW()),
    (1, 'Spotify', 5.99, CURRENT_DATE - INTERVAL '2 days', true, NOW()),
    (2, 'YouTube Premium', 9.99, CURRENT_DATE + INTERVAL '10 days', true, NOW());

INSERT INTO login_events (user_id, type, ip_address, country, device, created_at)
VALUES
    (1, 'LOGIN_SUCCESS', '192.168.1.10', 'Kazakhstan', 'Chrome Windows', NOW() - INTERVAL '5 minutes'),

    (1, 'LOGIN_FAILED', '192.168.1.10', 'Kazakhstan', 'Chrome Windows', NOW() - INTERVAL '4 minutes'),

    (1, 'LOGIN_FAILED', '192.168.1.10', 'Kazakhstan', 'Chrome Windows', NOW() - INTERVAL '3 minutes'),

    (2, 'LOGIN_FAILED', '88.12.34.56', 'Germany', 'Firefox Linux', NOW() - INTERVAL '2 minutes'),

    (3, 'LOGIN_SUCCESS', '201.44.22.11', 'Brazil', 'Safari Mac', NOW() - INTERVAL '1 minutes');

INSERT INTO risk_snapshots (user_id, score, level, reasons, calculated_at)
VALUES
    (1, 65, 'HIGH', 'Multiple failed logins; New IP address;', NOW()),
    (3, 90, 'CRITICAL', 'Too many login attempts; Multiple failed logins;', NOW());