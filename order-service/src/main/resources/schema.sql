use orderdb;

drop table if exists order_item;
drop table if exists orders;

create table orders (
	order_id integer auto_increment,
	username varchar(255),
	payment_mode varchar(255),
	payment_status varchar(255),
	delivery_status varchar(255),
	updated_at datetime(6),
	created_at datetime(6) not null,
	primary key (order_id)
) auto_increment = 10000000;


create table order_item (
	id int auto_increment,
	item varchar (255),
	order_id integer not null,
	unit_price float,
	quantity integer,
	effective_price float,
	updated_at datetime(6),
	created_at datetime(6) not null,
	primary key (id),
    foreign key (order_id) references orders(order_id)
);