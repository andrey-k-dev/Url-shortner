CREATE TABLE click_events (
    id              BIGSERIAL PRIMARY KEY,
    event_id        UUID NOT NULL UNIQUE,
    short_code      VARCHAR(32) NOT NULL,
    original_url    VARCHAR(2048),
    ip              VARCHAR(45),
    user_agent      VARCHAR(512),
    referer         VARCHAR(1024),
    clicked_at      TIMESTAMP NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_click_events_short_code ON click_events (short_code);
CREATE INDEX idx_click_events_clicked_at ON click_events (clicked_at);