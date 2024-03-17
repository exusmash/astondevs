INSERT INTO roles (name)
VALUES ('Admin'),       -- 1
       ('Manager'),     -- 2
       ('Developer'),   -- 3
       ('Salesperson'), -- 4
       ('Support'); -- 5

INSERT INTO products (name)
VALUES ('Laptop'),     -- 1
       ('Smartphone'), -- 2
       ('Headphones'), -- 3
       ('Printer'); -- 4

INSERT INTO employees (firstname, lastname, role_id)
VALUES ('John', 'Doe', 1),    -- 1
       ('Alice', 'Smith', 2), -- 2
       ('Bob', 'Johnson', 3), -- 3
       ('Eva', 'Miller', 3),  -- 4
       ('Mike', 'Wilson', 3), -- 5
       ('Petr', 'White', 4),  -- 6
       ('Sam', 'Moe', 5); -- 7

INSERT INTO employee_product_link (employee_id, product_id)
VALUES (1, 1), -- 1
       (2, 1), -- 2
       (3, 2), -- 3
       (4, 2), -- 4
       (5, 2), -- 5
       (6, 1), -- 6
       (6, 3), -- 6
       (7, 4); -- 7

INSERT INTO access_log (description, employee_id)
VALUES ('Accessed employee records', 1),     -- 1
       ('Modified customer information', 1), -- 2
       ('Logged in to the system', 2),       -- 3
       ('Generated financial report', 2),    -- 4
       ('Reviewed sales data', 3),           -- 5
       ('Updated inventory records', 4),     -- 6
       ('Checked security logs', 5),         -- 7
       ('Processed online orders', 6),       -- 8
       ('Configured system settings', 7); -- 9