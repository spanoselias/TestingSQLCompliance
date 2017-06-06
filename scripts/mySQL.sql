


DROP TABLE IF EXISTS
R1,
R2,
R3,
R4,
R5;

CREATE TABLE R1 ( -- df: size=4
       A smallint, -- df: size=2
       B smallint, -- df: size=2
       UNIQUE(A,B)
);

CREATE TABLE R2 ( -- df: size=4
       A smallint, -- df: size=2
       B smallint, -- df: size=2
       UNIQUE(A,B)
);

CREATE TABLE R3 ( -- df: size=4
       A smallint, -- df: size=2
       B smallint, -- df: size=2
       UNIQUE(A,B)
);

CREATE TABLE R4 ( -- df: size=4
       A smallint, -- df: size=2
       B smallint, -- df: size=2
       UNIQUE(A,B)
);

CREATE TABLE R5 ( -- df: size=4
       A smallint, -- df: size=2
       B smallint, -- df: size=2
       UNIQUE(A,B)
);


LOAD DATA LOCAL INFILE 'R1.csv' INTO TABLE R1 FIELDS TERMINATED BY ',';
LOAD DATA LOCAL INFILE 'R2.csv' INTO TABLE R2 FIELDS TERMINATED BY ',';
LOAD DATA LOCAL INFILE 'R3.csv' INTO TABLE R3 FIELDS TERMINATED BY ',';
LOAD DATA LOCAL INFILE 'R4.csv' INTO TABLE R4 FIELDS TERMINATED BY ',';
LOAD DATA LOCAL INFILE 'R5.csv' INTO TABLE R5 FIELDS TERMINATED BY ',';