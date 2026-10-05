CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id UUID NOT NULL,
    location_id UUID NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT uk_inventory_product_location UNIQUE (product_id, location_id)
);

CREATE TABLE stock_moviment (
    id_moviment BIGSERIAL PRIMARY KEY,
    product_id UUID NOT NULL,
    location_id UUID NOT NULL,
    destination_location_id UUID,
    moviment_type VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    previous_quantity INTEGER NOT NULL,
    new_quantity INTEGER NOT NULL,
    reason VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE reserves (
    id_reserva BIGSERIAL PRIMARY KEY,
    product_id UUID NOT NULL,
    location_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    reserved_at TIMESTAMP,
    expiresAt TIMESTAMP,
    order_id UUID
);