CREATE TABLE purchase_orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(30) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL REFERENCES suppliers(id),
    warehouse_id BIGINT NOT NULL REFERENCES warehouses(id),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    total_amount DECIMAL(14, 2),
    created_by VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    received_at TIMESTAMP,
    CONSTRAINT chk_order_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'RECEIVED', 'CANCELLED'))
);

CREATE INDEX idx_purchase_orders_number ON purchase_orders(order_number);
CREATE INDEX idx_purchase_orders_supplier ON purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_warehouse ON purchase_orders(warehouse_id);
CREATE INDEX idx_purchase_orders_status ON purchase_orders(status);
CREATE INDEX idx_purchase_orders_created ON purchase_orders(created_at);
