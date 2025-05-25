drop table if exists wishlist;

create table wishlist (
	id integer auto_increment,
	username varchar(255),
	item_name varchar(255),
	price float(53) not null,
	primary key (id),
	created_at datetime(6) not null,
	constraint uc_username_itemname unique (username, item_name)
);