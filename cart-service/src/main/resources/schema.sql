
use cartdb;

drop table if exists coupon;
drop table if exists cart;

create table cartdb.coupon (
	coupon_code varchar(50) primary key,
	discount varchar(50) not null
);

create table cart(
	id integer auto_increment,
	username varchar(255),
	item_name varchar(255),
	unit_price double not null,
	quantity int default 1,
	created_at datetime not null,
	updated_at datetime,
	primary key (id),
	constraint uc_username_itemname unique (username, item_name)
);