
use cartdb;

drop table if exists coupon;

create table cartdb.coupon (
	coupon_code varchar(50) primary key,
	discount varchar(50) not null
);

create table if not exists cart(
	username varchar(255),
	item varchar(255),
	unit_price double not null,
	quantity int default 1,
	created_at datetime not null,
	updated_at datetime,
	primary key (username, item)
);