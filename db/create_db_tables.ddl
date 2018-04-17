create table users(
	id int not null,
	is_child tinyint(1),
	user_name varchar(255),
	age int,

	primary key (id)
);

create table user_action(
	id int not null auto_increment,
	action_id int not null,
	user_id int not null,
    activity_id int,

    primary key(id)
);

create table user_words(
	id int not null auto_increment,
	user_id int not null,
    activity_id int not null,
    word varchar(256),

    primary key(id)
);