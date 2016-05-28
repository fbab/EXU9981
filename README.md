EXU9981A
==============
This project allows to load a SQL file stored in :
* src/main/resources/ddl.sql

ddl.sql can store CREATE TABLE (DDL) and INSERT (DML) instructions. 


* > cd /tmp
* > git clone https://github.com/fbab/EXU9981
* > cd /tmp/EXU9981
* > mvn install
* > mvn jetty:run
* > browser> locahost:8080
=> display Vaadin application
* > browser> locahost:8082
=> display H2 console

