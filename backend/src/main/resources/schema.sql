CREATE TABLE IF NOT EXISTS urls (
     id serial PRIMARY KEY,
     short_url varchar(50) NOT NULL,
     url varchar(50) NOT NULL
);