TRUNCATE users, accounts, transfers CASCADE;


Insert into users (user_id, username, password_hash)
values (1001, 'user1', '0000');

insert into accounts(account_id, user_id, balance)
values (2001, 1001, '500');


