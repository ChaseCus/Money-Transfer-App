TRUNCATE users, accounts, transfer_statuses, transfers CASCADE;


Insert into users (user_id, username, password_hash)
values (1001, 'user1', '0000'),
        (1002, 'user2', '0001');

insert into accounts(account_id, user_id, balance)
values (2001, 1001, '500'),
        (2002, 1002, '1000');

insert into transfer_statuses(transfer_status_id, transfer_status_desc)
values (1, 'Approved'),
        (2, 'Rejected');
           

insert into transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
values (4001, 1, 1, 2001, 2002, '300'),
        (4002, 1, 1, 2002, 2001, '50');


