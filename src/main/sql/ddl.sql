CREATE TABLE users
(
  id text NOT NULL,
  name text NOT NULL,
  CONSTRAINT users_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);