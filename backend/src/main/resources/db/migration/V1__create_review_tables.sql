CREATE TABLE reviews (
                         id              BIGSERIAL PRIMARY KEY,
                         repo_full_name  VARCHAR(255) NOT NULL,
                         pr_number       INTEGER      NOT NULL,
                         pr_title        VARCHAR(500),
                         pr_url          VARCHAR(500),
                         summary         TEXT,
                         files_reviewed  INTEGER      NOT NULL DEFAULT 0,
                         truncated       BOOLEAN      NOT NULL DEFAULT FALSE,
                         created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE findings (
                          id          BIGSERIAL PRIMARY KEY,
                          review_id   BIGINT       NOT NULL REFERENCES reviews (id) ON DELETE CASCADE,
                          file        VARCHAR(500),
                          line        INTEGER,
                          severity    VARCHAR(20)  NOT NULL,
                          message     TEXT         NOT NULL,
                          suggestion  TEXT
);

CREATE INDEX idx_findings_review_id ON findings (review_id);