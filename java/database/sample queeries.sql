SELECT * FROM transfers
WHERE ? IN (account_from,account_to)

insert into transfers(transfer_id ,transfer_type_id,transfer_status_id,
account_from, account_to, amount)
values (100,2,2,2,4,20.00)


insert into transfers(transfer_id ,transfer_type_id,transfer_status_id,
account_from, account_to, amount)
values (101,2,2,4,2,20.00)

update accounts where balance = (balance-10)