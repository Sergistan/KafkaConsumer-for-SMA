-- need this for gen_random_uuid() in changelog work properly
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
create table if not exists users
(
    id       bigserial primary key,
    name     varchar(255) not null unique,
    password varchar(255) not null,
    email    varchar(255) not null unique,
    role     varchar(256)
);
insert into users (id,name, password, email, role)
VALUES (1,'Sergey', '$2a$12$uhKy.MGqmfYcgbkTd3ZR.eyXKVrU3gm0epHhSy6cfXzydbX4n3Ws2', 'sergistan.utochkin@yandex.ru','ROLE_USER'); /* password 111 */
create table if not exists followers
(
    user_id     int references users (id),
    follower_id int references users (id),
    primary key (user_id, follower_id)
);
create table if not exists posts
(
    id          bigserial primary key,
    description varchar(255) not null,
    message     varchar(255) not null,
    image_link  varchar(512) unique,
    image_name  varchar(255) unique,
    created_at  timestamp,
    user_id     bigserial    references users (id) on delete set null
);
insert into posts (id,description, message, image_link, image_name, created_at, user_id)
VALUES (1,'New description', 'New message', null, null, '2024-03-19 16:58', 1);
create table if not exists chats
(
    id           bigserial primary key,
    created_at   timestamp,
    last_message varchar(255) not null
);
create table if not exists messages
(
    id        bigserial primary key,
    sent_at   timestamp,
    text      varchar(255) not null,
    sender_id bigserial    references users (id) on delete set null,
    chat_id   bigserial    references chats (id) on delete set null
);
