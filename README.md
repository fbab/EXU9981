EXU9981A
==============

Introduction
===
This project allows to load at application startup (mvn jetty:run) a SQL file stored in :
* src/main/resources/ddl.sql

ddl.sql can store CREATE TABLE (DDL) and INSERT (DML) instructions. 

The db can be browsed with H2 console available on 8082 port.

Procedure
===
* > cd /tmp
* > git clone https://github.com/fbab/EXU9981
* > cd /tmp/EXU9981
* > mvn install
* > mvn jetty:run
* > browser> localhost:8080
=> display Vaadin application
* > browser> localhost:8082
=> display H2 console

