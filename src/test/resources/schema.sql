DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post;

create table if not exists post
(
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title       varchar(256) not null,
    text        varchar(256) not null,
    tags        varchar(256) array,
    likes_count integer default 0
);

create table if not exists image
(
post_id   integer references post (id),
file_name varchar(256) not null,
data      bytea
);

create table if not exists comment
(
    id      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text    varchar(256) not null,
    post_id integer references post (id)
);
