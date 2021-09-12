/* inside connection with root user
DROP USER 'homework'@'localhost';
CREATE USER 'ironhacker'@'localhost' IDENTIFIED BY 'Ir0nh4ck3r!';
GRANT ALL PRIVILEGES ON midterm_database.* TO 'ironhacker'@'localhost';
GRANT ALL PRIVILEGES ON midterm_database_test.* TO 'ironhacker'@'localhost';
*/
DROP SCHEMA IF EXISTS midterm_database_test;
CREATE SCHEMA midterm_database_test;

DROP SCHEMA IF EXISTS midterm_database;
CREATE SCHEMA midterm_database;
USE midterm_database;

DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255)
);

DROP TABLE IF EXISTS role;

CREATE TABLE role (
    id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

INSERT INTO user (username, password) VALUES
("admin","$2a$10$MSzkrmfd5ZTipY0XkuCbAejBC9g74MAg2wrkeu8/m1wQGXDihaX3e");

INSERT INTO role (name, user_id) VALUES
("ADMIN", 1);

DROP TABLE IF EXISTS address;

CREATE TABLE address (
	id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    direction VARCHAR(255),
    location VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    creation_date DATE,
    modification_date DATE
);

DROP TABLE IF EXISTS person_database;

CREATE TABLE person_database (
	id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    date_of_birth DATE,
    primary_address INT,
    mailing_address VARCHAR(255),
    creation_date DATE,
    modification_date DATE,
    FOREIGN KEY (primary_address) REFERENCES address (id)
);

DROP TABLE IF EXISTS checking;

CREATE TABLE checking (
	id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    amount DECIMAL,
    currency VARCHAR(255),
    owner_id INT,
    other_owner_id INT,
    penalty_fee DECIMAL,
    status VARCHAR(255),
    secret_key VARCHAR(255),
    minimum_balance DECIMAL,
    monthly_maintenance_fee DECIMAL,
    creation_date DATE,
    modification_date DATE,
    FOREIGN KEY (owner_id) REFERENCES person_database (id),
	FOREIGN KEY (other_owner_id) REFERENCES person_database (id)
);

DROP TABLE IF EXISTS student;

CREATE TABLE student (
	id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    amount DECIMAL,
    currency VARCHAR(255),
    owner_id INT,
    other_owner_id INT,
    penalty_fee DECIMAL,
	status VARCHAR(255),
    secret_key VARCHAR(255),
    creation_date DATE,
    modification_date DATE,
    FOREIGN KEY (owner_id) REFERENCES person_database (id),
	FOREIGN KEY (other_owner_id) REFERENCES person_database (id)
);

DROP TABLE IF EXISTS saving;

CREATE TABLE saving (
	id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    amount DECIMAL,
    currency VARCHAR(255),
    owner_id INT,
    other_owner_id INT,
    minimum_balance DECIMAL,
    penalty_fee DECIMAL,
    status VARCHAR(255),
    secret_key VARCHAR(255),
    interest_rate DECIMAL,
    creation_date DATE,
    modification_date DATE,
    FOREIGN KEY (owner_id) REFERENCES person_database (id),
	FOREIGN KEY (other_owner_id) REFERENCES person_database (id)
);

DROP TABLE IF EXISTS credit_card;

CREATE TABLE credit_card (
	id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    amount DECIMAL,
    currency VARCHAR(255),
    owner_id INT,
    other_owner_id INT,
	penalty_fee DECIMAL,
    credit_limit DECIMAL,
    secret_key VARCHAR(255),
    interest_rate DECIMAL,
    creation_date DATE,
    modification_date DATE,
    FOREIGN KEY (owner_id) REFERENCES person_database (id),
	FOREIGN KEY (other_owner_id) REFERENCES person_database (id)
);

DROP TABLE IF EXISTS movement;

CREATE TABLE movement (
	id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    account_id INT,
    amount DECIMAL,
    balance_before DECIMAL,
    balance_after DECIMAL,
    movement_type VARCHAR(255),
    order_date DATE,
    modification_date DATE,
    FOREIGN KEY (account_id) REFERENCES checking(id)
);