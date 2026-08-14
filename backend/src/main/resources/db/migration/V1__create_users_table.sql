CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_phone_number UNIQUE (phone_number),
    CONSTRAINT chk_users_role
        CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT chk_users_status
        CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DEACTIVATED'))
);