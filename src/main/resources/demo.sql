--This script is used for unit test cases, DO NOT CHANGE!

DROP TABLE IF EXISTS Party;

CREATE TABLE Party
(
  Id              LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Username        VARCHAR(30)                     NOT NULL,
  Email           VARCHAR(30)                     NOT NULL,
  CreatePartyDate VARCHAR(30)
);

CREATE UNIQUE INDEX idx_pr on Party (Username, Email);

INSERT INTO Party (Username, Email, CreatePartyDate)
VALUES ('Ebrahim', 'ebrahim@gmail.com', '2019-05-06T18:57:18.936Z');
INSERT INTO Party (Username, Email, CreatePartyDate)
VALUES ('Khosravani', 'ekhosravni@gmail.com', '2019-05-06T18:57:18.936Z');
INSERT INTO Party (Username, Email, CreatePartyDate)
VALUES ('Joe', 'joe_k@gmail.com', '2019-05-06T18:57:18.936Z');
INSERT INTO Party (Username, Email, CreatePartyDate)
VALUES ('test2', 'test_k@gmail.com', '2019-05-06T18:57:18.936Z');

DROP TABLE IF EXISTS Account;

CREATE TABLE Account
(
  Id                LONG AUTO_INCREMENT PRIMARY KEY NOT NULL,
  PartyId           VARCHAR(30)                     NOT NULL,
  Balance           DECIMAL(19, 4),
  Iban              VARCHAR(30)                     NOT NULL,
  CurrencyCodeType  VARCHAR(10),
  AccountStatusType VARCHAR(30)                     NOT NULL,
  CreateAccountDate VARCHAR(30)                     NOT NULL,
  UpdateAccountDate VARCHAR(30)
);

CREATE UNIQUE INDEX idx_acc on Account (PartyId, Iban);

INSERT INTO Account (PartyId, Balance, Iban, CurrencyCodeType, AccountStatusType, CreateAccountDate, UpdateAccountDate)
VALUES ('Ebrahim', 100.0000, 'UK1234', 'USD', 'ACTIVE', '2019-05-06T18:57:18.936Z', '2019-05-06T20:18:59.142Z');
INSERT INTO Account (PartyId, Balance, Iban, CurrencyCodeType, AccountStatusType, CreateAccountDate, UpdateAccountDate)
VALUES ('Khosravani', 200.0000, 'UK1515', 'USD', 'ACTIVE', '2019-05-06T18:57:18.936Z', '2019-05-06T20:18:59.142Z');
INSERT INTO Account (PartyId, Balance, Iban, CurrencyCodeType, AccountStatusType, CreateAccountDate, UpdateAccountDate)
VALUES ('Joe', 500.0000, 'UK9121', 'EUR', 'ACTIVE', '2019-05-06T18:57:18.936Z', '2019-05-06T20:18:59.142Z');
INSERT INTO Account (PartyId, Balance, Iban, CurrencyCodeType, AccountStatusType, CreateAccountDate, UpdateAccountDate)
VALUES ('Joe', 500.0000, 'UK4123', 'EUR', 'ACTIVE', '2019-05-06T18:57:18.936Z', '2019-05-06T20:18:59.142Z');
INSERT INTO Account (PartyId, Balance, Iban, CurrencyCodeType, AccountStatusType, CreateAccountDate, UpdateAccountDate)
VALUES ('test2', 500.0000, 'UK1298', 'GBP', 'ACTIVE', '2019-05-06T18:57:18.936Z', '2019-05-06T20:18:59.142Z');
INSERT INTO Account (PartyId, Balance, Iban, CurrencyCodeType, AccountStatusType, CreateAccountDate, UpdateAccountDate)
VALUES ('Neo', 500.0000, 'UK9821', 'GBP', 'ACTIVE', '2019-05-06T18:57:18.936Z', '2019-05-06T20:18:59.142Z');


DROP TABLE IF EXISTS TransactionPayment;

CREATE TABLE TransactionPayment
(
  Id                   LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Amount               DECIMAL(19, 4)                  NOT NULL,
  Stan                 VARCHAR(30)                     NOT NULL,
  SourceAccountId      LONG                            NOT NULL,
  DestinationAccountId LONG                            NOT NULL,
  PersistenceTime      VARCHAR(30)
);

CREATE UNIQUE INDEX idx_tran on TransactionPayment (Id, Stan);