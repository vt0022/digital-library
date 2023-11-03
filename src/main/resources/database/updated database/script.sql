create table category
(
    category_id   binary(16)  not null
        primary key,
    category_name varchar(50) not null,
    created_at    datetime(6) null,
    is_deleted    bit         not null,
    slug          varchar(50) not null,
    updated_at    datetime(6) null,
    constraint UK_hqknmjh5423vchi4xkyhxlhg2
        unique (slug),
    constraint UK_lroeo5fvfdeg4hpicn4lw7x9b
        unique (category_name)
);

create table field
(
    field_id   binary(16)   not null
        primary key,
    created_at datetime(6)  null,
    field_name varchar(100) not null,
    is_deleted bit          not null,
    slug       varchar(100) not null,
    updated_at datetime(6)  null,
    constraint UK_bn39g9jasccxlt1n07axdryof
        unique (slug),
    constraint UK_f8mr6kl8yhhwvv1j7ywr8hout
        unique (field_name)
);

create table organization
(
    org_id     binary(16)   not null
        primary key,
    created_at datetime(6)  null,
    is_deleted bit          not null,
    org_name   varchar(100) not null,
    updated_at datetime(6)  null,
    slug       varchar(100) not null,
    constraint UK_8xwh6htjvm2c39c2se8hbptj9
        unique (org_name)
);

create table role
(
    id        binary(16)   not null
        primary key,
    role_name varchar(255) not null,
    constraint UK_iubw515ff0ugtm28p8g3myt0h
        unique (role_name)
);

create table user
(
    user_id       binary(16)   not null
        primary key,
    created_at    datetime(6)  null,
    date_of_birth datetime(6)  null,
    email         varchar(50)  not null,
    first_name    varchar(50)  null,
    gender        int          not null,
    image         varchar(255) null,
    is_deleted    bit          not null,
    is_verified   bit          not null,
    last_name     varchar(50)  null,
    middle_name   varchar(50)  null,
    password      varchar(255) not null,
    phone         varchar(15)  null,
    updated_at    datetime(6)  null,
    username      varchar(50)  null,
    org_id        binary(16)   null,
    role_id       binary(16)   null,
    constraint UK_ob8kqyqqgmefl0aco34akdtpe
        unique (email),
    constraint UK_sb8bbouer5wak8vyiiy4pf2bx
        unique (username),
    constraint FK9usot4gododq1u90duvulb92d
        foreign key (role_id) references role (id),
    constraint FKbxldebv5vj7nbdlpuisg67q9n
        foreign key (org_id) references organization (org_id)
);

create table document
(
    doc_id           binary(16)   not null
        primary key,
    author           varchar(100) null,
    doc_introduction text         null,
    doc_name         varchar(255) not null,
    download_url     varchar(255) not null,
    is_deleted       bit          not null,
    is_internal      bit          not null,
    is_private       bit          not null,
    slug             varchar(255) not null,
    thumbnail        varchar(255) null,
    total_favorite   int          not null,
    total_view       int          not null,
    updated_at       datetime(6)  null,
    uploaded_at      datetime(6)  null,
    view_url         varchar(255) not null,
    category_id      binary(16)   null,
    field_id         binary(16)   null,
    org_id           binary(16)   null,
    uploaded_by      binary(16)   null,
    verified_by      binary(16)   null,
    verified_status  int          not null,
    note             varchar(255) null,
    constraint UK_goy65bsr4v4b5m0c2igrp16u3
        unique (slug),
    constraint FKc0g4k1v0ukw2hi3hevt8avsi8
        foreign key (org_id) references organization (org_id),
    constraint FKc0xy5uahkc120wpoj0cugd2xj
        foreign key (field_id) references field (field_id),
    constraint FKscp4rcbvka45vmm81pdbsqiw2
        foreign key (verified_by) references user (user_id),
    constraint FKsfim0o1kybbx5cryeg7lmrug7
        foreign key (uploaded_by) references user (user_id),
    constraint FKtmy4w0fql55r22q8aqxh3ulpj
        foreign key (category_id) references category (category_id)
);

create table comment
(
    comment_id binary(16)   not null
        primary key,
    content    varchar(255) not null,
    created_at datetime(6)  null,
    is_deleted bit          not null,
    updated_at datetime(6)  null,
    doc_id     binary(16)   null,
    user_id    binary(16)   null,
    constraint FK2rptw66w78rbscdi3ytb4om3f
        foreign key (doc_id) references document (doc_id),
    constraint FKsn1eiuccx1w2fdlj42s1kl75w
        foreign key (user_id) references user (user_id)
);

create table favorite
(
    doc_id   binary(16) not null,
    user_id  binary(16) not null,
    is_liked bit        not null,
    primary key (doc_id, user_id),
    constraint FK68kst37l7af0e8d93odpcr6qg
        foreign key (user_id) references user (user_id),
    constraint FKj77q4uojs8a6ddgtgeusenwms
        foreign key (doc_id) references document (doc_id)
);

create table review
(
    review_id  binary(16)   not null
        primary key,
    content    varchar(255) null,
    created_at datetime(6)  null,
    star       int          not null,
    updated_at datetime(6)  null,
    doc_id     binary(16)   null,
    user_id    binary(16)   null,
    constraint FK3hlb3mlxoancjy2o664jsht75
        foreign key (doc_id) references document (doc_id),
    constraint FKp62gdxif9h9dtdwcyf5b7gdi8
        foreign key (user_id) references user (user_id)
);

create table save
(
    doc_id   binary(16) not null,
    user_id  binary(16) not null,
    is_saved bit        not null,
    primary key (doc_id, user_id),
    constraint FKeljyato2dhrfe6yot5utak2oh
        foreign key (doc_id) references document (doc_id),
    constraint FKhlbwd9tkm7pm2e1yft5l6kutm
        foreign key (user_id) references user (user_id)
);


