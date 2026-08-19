CREATE TABLE links(
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(30) NOT NULL UNIQUE,
    original_url VARCHAR(2048) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    redirect_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    expired_at TIMESTAMP
)