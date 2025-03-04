create database if not exists wishlistdb;

create table if not exists wishlist (	
	username varchar(255) primary key,
	itemname varchar(255),
	price float
);



