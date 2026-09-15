ALTER TABLE notifications
    RENAME COLUMN user_id TO recipient_id;

ALTER TABLE notifications
    RENAME COLUMN sender_id TO actor_id;

ALTER TABLE notifications
    ADD COLUMN reference_type VARCHAR(50);

ALTER TABLE notifications
    ALTER COLUMN is_read SET DEFAULT FALSE;

ALTER TABLE notifications
    ALTER COLUMN is_read SET NOT NULL;

ALTER TABLE notifications
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;

DROP INDEX IF EXISTS idx_notifications_user_id;
DROP INDEX IF EXISTS idx_notifications_sender_id;

CREATE INDEX idx_notifications_recipient
    ON notifications(recipient_id, is_read, created_at DESC);