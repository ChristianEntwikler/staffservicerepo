CREATE ROLE sa WITH
	LOGIN
	SUPERUSER
	CREATEDB
	CREATEROLE
	INHERIT
	REPLICATION
	CONNECTION LIMIT -1
	PASSWORD 'sysdev123';

CREATE DATABASE staffdb
    WITH 
    OWNER = sa
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;
	

create table stafftbl
(
id serial not null primary key,
staffid varchar(50) not null,
username varchar(50) not null,
firstname varchar(50) not null,
surname varchar(50) not null,
middlename varchar(50),
dob varchar(20) not null,
gender varchar(20) not null,
emailaddress varchar(100),
phonenumber varchar(20),
address Text,
supervisorid varchar(50),
dateuploaded timestamp
);

CREATE UNIQUE INDEX ix_staffid
ON stafftbl(staffid, username);


create table activitylog
(
id serial not null primary key,
requestid varchar(50),
requesttype varchar(100),
username varchar(50),
description text,
ipaddress varchar(20),
dateuploaded timestamp
);