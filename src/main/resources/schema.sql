drop table if exists customers;
drop table if exists loans;
drop table if exists loanInstallments;

create table if not exists customers (
    id UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255),
    credit_limit DECIMAL(10,2),
    used_credit_limit DECIMAL(10,2)
);

create table if not exists loans (
    id UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    customer_id UUID,
    loan_amount DECIMAL(10,2),
    number_of_installment INT,
    create_date TIMESTAMP,
    is_paid BOOLEAN
);

create table if not exists loanInstallments (
    id UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    loan_id UUID,
    amount DECIMAL(10,2),
    paid_amount DECIMAL(10,2),
    due_date DATE,
    payment_date DATE,
    is_paid BOOLEAN
);

insert into customers values ('CE6B02CB-1479-4F1A-9F08-9098C55BC871', 'John', 'Doe', 10000.0, 420.0);

insert into loans values ('CE3DA7AC-B131-4037-AAEE-309424AAF4EA', 'CE6B02CB-1479-4F1A-9F08-9098C55BC871', 300.0, 6, '2024-12-12T14:05:00.000000', false);

insert into loanInstallments values ('BF45EBDE-4668-45B5-9E74-49E82DC8BF5B', 'CE3DA7AC-B131-4037-AAEE-309424AAF4EA', 55.0, 0.0, '2025-01-01', null, false);
insert into loanInstallments values ('A0DAD5D8-949D-46D4-8E7E-7A2C567F1D8E', 'CE3DA7AC-B131-4037-AAEE-309424AAF4EA', 55.0, 0.0, '2025-02-01', null, false);
insert into loanInstallments values ('66CB30B2-AEE7-4DC0-9695-433700667CED', 'CE3DA7AC-B131-4037-AAEE-309424AAF4EA', 55.0, 0.0, '2025-03-01', null, false);
insert into loanInstallments values ('2D99A9F1-6A90-4A56-BDB0-8C1255F8F1A8', 'CE3DA7AC-B131-4037-AAEE-309424AAF4EA', 55.0, 0.0, '2025-04-01', null, false);
insert into loanInstallments values ('3064E497-6302-4283-A8B0-78517527F4D9', 'CE3DA7AC-B131-4037-AAEE-309424AAF4EA', 55.0, 0.0, '2025-05-01', null, false);
insert into loanInstallments values ('BEBA88C2-340D-4539-993C-EBA51999FE2F', 'CE3DA7AC-B131-4037-AAEE-309424AAF4EA', 55.0, 0.0, '2025-06-01', null, false);


insert into loans values ('D6ADF188-8B04-47A8-A45C-3FD04E9A87BB', 'CE6B02CB-1479-4F1A-9F08-9098C55BC871', 120.0, 3, '2024-12-14T16:08:00.000000', false);

insert into loanInstallments values ('4A1082EE-170F-4AF4-ADCC-07CA4C8E2793', 'D6ADF188-8B04-47A8-A45C-3FD04E9A87BB', 44.0, 0.0, '2024-12-01', null, false);
insert into loanInstallments values ('F6E372ED-8C13-4290-B22F-226936310919', 'D6ADF188-8B04-47A8-A45C-3FD04E9A87BB', 44.0, 0.0, '2025-01-01', null, false);
insert into loanInstallments values ('AE32C378-836C-4293-87BD-31A5531A2C9C', 'D6ADF188-8B04-47A8-A45C-3FD04E9A87BB', 44.0, 0.0, '2025-02-01', null, false);