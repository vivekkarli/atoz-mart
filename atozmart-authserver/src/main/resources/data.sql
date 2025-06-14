delete from atozmart_authserver_db.app_user 
where username in ('order_service', 'profile_service', 'atozmart_gatewayserver');

insert into atozmart_authserver_db.app_user
(mail, username, password, roles, mobile_no, mobile_no_verified, email_verified, created_at)
values 
('vivekkarli7@gmail.com', 'order_service', '$2a$12$LT.lAhdpreqEjKFpH/DZa.4Ohy1LUGEKfzj7bZ1MZpW9BWUL.MIdO',
'ROLE_APP', null, 0, 1, now()),
('vivekkarli7@gmail.com', 'profile_service', '$2a$12$UgZyBQBuzr22JjPZD2KoU.5MNDqRSLJkRdQPXJ7k45LAcDJRrvBQu',
'ROLE_APP', null, 0, 1, now());
('vivekkarli7@gmail.com', 'atozmart_gatewayserver', '$2a$12$TuPAJYV8nkWmNvbj0HG.d.NbgEqkc8hpyfpNFR1ALvy1Kpyk58ND2',
'ROLE_APP', null, 0, 1, now());