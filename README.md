<p align = "center">
 Implementation of a complete framework for evaluating the SQL-Compliance of current DBMSs

<a>
<p align = "center">
<img src="https://www.nuodb.com/sites/default/files/graphics/icons/SQL-icon-transparent.png" width="100" height="100"/>
</a>


This project aims to build a complete framework for testing the SQL-Compliance of current DBMSs ( PostgreSQL, MySQL, IBM DB2, MS Server sql, Oracle Database). 
The core component of the framework is consisted by the random SQL generator tool, the comparison tool and the datafiller for generate random realistic data. 
The project is implemented in Java Programming Language. Moreover, this framework is implemented in such a way that a new DBMS can be added efficiently without 
the need to change the whole code. 

### Compilation & Run instructions using PostgreSQL 

#### Linux
```javascript
 Compile: javac -cp '.:postgresql-42.1.1.jar' SQLEngine.java
 Run: java -cp '.:postgresql-42.1.1.jar' SQLEngine
```

#### Windows
```javascript
 Compile: javac -cp '.;postgresql-42.1.1.jar' SQLEngine.java
 Run: java -cp '.;postgresql-42.1.1.jar' SQLEngine
```


#### Note: The following parameters can be passed as arguments to the random query tool:
```javascript
-maxTablesFrom, -maxAttrSel, -maxCondWhere, -maxAttrGrpBy, -probWhrConst,
-repAlias, -nestLevel, -arithCompSel, -distinct, -stringInSel,-stringInWhere, -rowcompar,
-DBMS, -user, -pass, -dbName, -isNULL,-isSelectAll
```

### Example 1 of passing some parameters as arguments 

```javascript
 Compile: javac -cp '.:postgresql-42.1.1.jar' SQLEngine.java
 Run: java -cp '.:postgresql-42.1.1.jar' SQLEngine -pass testing1  -DBMS mysql -dbname  teststr -user root
```

### Example 2
```javascript
 javac -cp ''.;postgresql-42.1.1.jar' SQLEngine.java -maxTablesFrom,! 5 -maxCondWhere 3 -maxAttrSel 4
```




### Currently supported DBMSs:

```javascript
 > MySql
 > Oracle
 > PostgreSQL
 > IBM DB2
 > MSSQL Server
```

### Dependencies 

```javascript
> Linux or Windows
> Java 8 Update 92 or higher 
> Datafiller 
  
  ```
  
 ### How to generate realistic data using datafiller 
 
 ```javascript
 python datafiller.py --drop -n 0.33 schema.sql  > data.sql
   ```
    
### Goals

The goals of this project are summarized as follow:

* Random Query Generator Tool

* Comparison Tool

* Datafiller Tool

* Highlight main differences among current DBMSs





