create table if not exists `task_huge_path`
(
    `id`         varchar(36)  not null primary key,
    `task_id`    varchar(36)  not null,
    `huge_paths` varchar(600) not null
);

-- create table if not exists `task_huge_path`
-- (
--     `id`        varchar(36) not null primary key,
--     `task_id`   varchar(36) not null
-- );