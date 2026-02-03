
delete from atozmart_authserver_db.app_user_role
where role in ('app', 'user', 'admin', 'agent');

delete from atozmart_authserver_db.app_role
where role in ('app', 'user', 'admin', 'agent');

delete from atozmart_authserver_db.app_user 
where username in ('order_service', 'profile_service', 'atozmart_gatewayserver', 'vivek_karli', 'test_agent', 'test_agent_2', 'demo_admin', 'demo_user');

insert into atozmart_authserver_db.app_user
(username, password, mail, mobile_no, email_verified, mobile_no_verified, created_at)
values 
('order_service', '$2a$12$LT.lAhdpreqEjKFpH/DZa.4Ohy1LUGEKfzj7bZ1MZpW9BWUL.MIdO', 'vivekkarli7@gmail.com', null, 1, 0, now()),
('profile_service', '$2a$12$UgZyBQBuzr22JjPZD2KoU.5MNDqRSLJkRdQPXJ7k45LAcDJRrvBQu', 'vivekkarli7@gmail.com', null, 1, 0, now()),
('atozmart_gatewayserver', '$2a$12$TuPAJYV8nkWmNvbj0HG.d.NbgEqkc8hpyfpNFR1ALvy1Kpyk58ND2', 'vivekkarli7@gmail.com', null, 1, 0, now()),
('vivek_karli', '$2a$12$sxcPA2LF.ni/DXKDVG.6KusFeFBmuAc4gIja5tXdozlZQk0LjSNEK', 'vivekkarli7@gmail.com', null, 1, 1, now()),
('test_agent', '$2a$12$WwhHVc91Ux91kg2uWxk8veExowC7WOHUwVRGFb9NwVQ20SmWxpGJm', 'vivekkarli7@gmail.com', null, 1, 1, now()),
('test_agent_2', '$2a$12$r0o4k4SuTjxUraUjYPpd.OcDmDxBWlLly0VzmtVOvNM4.kyQCZ4lW', 'vivekkarli7@gmail.com', null, 1, 1, now()),
('demo_admin', '$2a$12$r0o4k4SuTjxUraUjYPpd.OcDmDxBWlLly0VzmtVOvNM4.kyQCZ4lW', 'vivekkarli7@gmail.com', null, 1, 1, now()),
('demo_user', '$2a$12$r0o4k4SuTjxUraUjYPpd.OcDmDxBWlLly0VzmtVOvNM4.kyQCZ4lW', 'vivekkarli7@gmail.com', null, 1, 1, now());

insert into atozmart_authserver_db.app_role
(role, description)
values
('app', 'internal services uses this role'),
('admin',  null),
('user',  null),
('agent',  null);

insert into atozmart_authserver_db.app_user_role
(role, username)
values
('app', 'order_service'),
('app', 'profile_service'),
('app', 'atozmart_gatewayserver'),
('admin', 'vivek_karli'),
('user', 'vivek_karli'),
('agent', 'test_agent'),
('agent', 'test_agent_2'),
('admin','demo_admin'),
('user','demo_user');