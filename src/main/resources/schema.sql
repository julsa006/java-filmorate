CREATE TABLE IF NOT EXISTS mpas
(
    id   INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(50)  NOT NULL,
    description  VARCHAR(200) NOT NULL,
    release_date DATE         NOT NULL,
    duration     INTEGER      NOT NULL,
    mpa_id       INTEGER      NOT NULL REFERENCES mpas (id)
);

CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS films_genres
(
    film_id  INTEGER NOT NULL REFERENCES films (id),
    genre_id INTEGER NOT NULL REFERENCES genres (id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(50) NOT NULL UNIQUE,
    login    VARCHAR(50) NOT NULL UNIQUE,
    name     VARCHAR(50) NOT NULL,
    birthday DATE        NOT NULL
);

CREATE TABLE IF NOT EXISTS users_relationships
(
    follower_id  INTEGER NOT NULL REFERENCES users (id),
    following_id INTEGER NOT NULL REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id INTEGER NOT NULL REFERENCES films (id),
    user_id INTEGER NOT NULL REFERENCES users (id),
    PRIMARY KEY (film_id, user_id)
);