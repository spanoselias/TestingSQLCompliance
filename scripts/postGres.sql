DROP TABLE IF EXISTS
R1,
R2,
R3,
R4,
R5;

CREATE TABLE R1
 ( -- df: size=4
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
