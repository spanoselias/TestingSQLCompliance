Readme file. Elias Spanos (s1669343)
------------------------------------

The src should contain the following folders with the associated files:
---------------------------------------------------------------------

The Comparison Tool directory with the following java classes: ComparisonTool.java, ConfComparison.java,ResInfo.java

The Engine directory contains the following java classes:
        Attribute.java, COMPARISON.java, ConParameters.java,
        FROM.java, FUNCTIONS.java, GROUPBY.java, HAVING.java,
        NESTCOMPARISON.java, QRYREPRES.java, Relation.java SELECT.java,
        SETOPERATORS.java, SQLQUERY.java, STRINGS.java,Utilities.java, WHERE.java

The log directory which contains some log files that are collected during the experiments.

The statistics directory that contains the following java class: Statistics.java

The src directory also contains the SQLEngine.java, comparisonTool.properties and config.properties. 

The directory lib which contains five libraries corresponding to the library of each DBMS.


Compilation & Execution Instructions
------------------------------------

Compilation Instructions:
-------------------------

Note: The Postgresql JDBC library is passed as an argument since the program
needs to retrieve the schema from PostgreSQL database. (Credentials need to be given
via configuration file or as arguments). In addition, the schema can be retrieved from
MySQL as well using ‘mysql-connector-java-5.1.41-bin.jar’

The following commands should be executed in the ‘src’ directory in conjunction
with PostgreSQL in order to retrieve the schema

Linux:
javac -cp '.:postgresql-42.1.1.jar' SQLEngine.java

Windows:
javac -cp '.;postgresql-42.1.1.jar' SQLEngine.java

Execution Instructions:
Linux:
java -cp '.:postgresql-42.1.1.jar' SQLEngine

Windows:
java -cp '.;postgresql-42.1.1.jar' SQLEngine

The following parameters can be passed as arguments to the random query tool:
-maxTablesFrom, -maxAttrSel, -maxCondWhere, -maxAttrGrpBy, -probWhrConst,
-repAlias, -nestLevel, -arithCompSel, -distinct, -stringInSel,-stringInWhere, -rowcompar,
-DBMS, -user, -pass, -dbName, -isNULL,-isSelectAll

Example1:
---------
javac -cp '.;postgresql-42.1.1.jar' SQLEngine.java -maxTablesFrom 5 -maxCondWhere 3 -maxAttrSel 4

Example2:
---------
javac -cp '.;postgresql-42.1.1.jar' SQLEngine.java -maxtablesfrom 5 -maxCondWhere 3 -maxAttrSel 4 -arithCompSel 0.5 -distinct  0.5

Note: The sumbol ; needs to be repalced with : if the above examples are executed on Linux platform.


Note: Any number of arguments can be passed to the program, and they are not
case-sensitive as it can be seen from the Example2.

The following steps should be followed in order to run the complete framework:
-Firstly, the environment should be set up. Hence, it should run on an environment
   with the following DBMSs installed: PostgreSQL Version 9.6, Microsoft SQL
   Server Express Edition 2016, IBM DB2 Express-C, Oracle Database 12c and
   MySQL Community Edition 5.7. (All the appropriate libraries are provided
   with the source code under lib directory). And the appropriate credential should
   be specified in the comparisonTool.properties.
  Both DBMS’ schemas and random generated data are provided under Script directory.
-Also, the open-source Datafiller can be used for generating realistic random
   data (https://www.cri.ensmp.fr/people/coelho/datafiller.html#
   downloade)

Linux:
javac -cp ’All_libraries_names’ SQLEngine.java

Windows:
javac -cp ’All_libraries_names’ SQLEngine.java -compTool,! noOfQueries

Execution Instructions:
Linux:
java -cp '.:postgresql-42.1.1.jar' SQLEngine

Windows:
java -cp '.;postgresql-42.1.1.jar' SQLEngine -compTool,! noOfQueries


Note: A log file will all the differences will be generated once the program execution
      is finished

Window Example Compilation:
--------------------------
javac -cp '.;postgresql-42.1.1.jar;db2jcc4.jar;mysql-connector-java-5.1.41-bin.jar;ojdbc7-12.1.0.2.jar;sqljdbc4.jar' SQLEngine

Window Example:
---------------
java -cp '.;postgresql-42.1.1.jar;db2jcc4.jar;mysql-connector-java-5.1.41-bin.jar;ojdbc7-12.1.0.2.jar;sqljdbc4.jar' SQLEngine -compTool 10
