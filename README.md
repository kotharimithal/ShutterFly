# Shutterfly Coding exercise - Kothari Mithal

## Requirements
mysql (version : 6.3 , This is the version I have used to solve this challenge but you can use older version too.)
java (version : 1.8)

You can execute this repository after installing above requirements.
You will have to update the DB Credentials in src/Database.java file.

## Mysql
Make sure SQL Server is running and if not you will need to start it. 
To check if mysql is running or not you can execute mysql.server status.

## Update Input
Place the input file events.txt in the input directory with events.

## Execute the code
To compile the code use the below command:
cd <Project Path>
javac -classpath "libs/json-20160810.jar:libs/mysql-connector-java-6.0.5.jar:src" src/*.java

To execute code after compiling use below command:
java -classpath "libs/json-20160810.jar:libs/mysql-connector-java-6.0.5.jar:src" Main

This will execute code and create DB Shutterfly and all the different tables if DB doesn't exist. 
Assumption is if DB is present then tables will be present too. 
Use the following command to delete table and then execute code:
drop database shutterfly;

##Checking the output
To check the output you can login to mysql using same credentials and then run 
use shutterFly; (execute this command in mysql)
select * from table_name; to see the data in that table.

## Libraries used
I have used mysql-connector lib to connect to mysql using JAVA jdbc. 
I have also used json.jar file to read and parse the events (json) correctly. 

## Input:
I have assumed data will be passed in JSONArray format even for a single event.
If event has missing value then it will be written to rejects.txt file in output directory.

##LTV
I have calculated LTV using dates from one year to date of execution and plugging those dates in the query. For calculating LTV for each week I have implmemented a method which can give us the start and end date of each week in a past year in the HashMap.

## PERFORMANCE and FUTURE CHANGES:
Fields like tags can be made as a child table of SiteVisit.java.
More checks can be added to database related operations and further code can be refactored. 
Performance can be improved by adding by stress testing and using other dbs. 
Also we could new tables in database and scheduler to run this as batch process to process large amount of data 
	and also to improve performance we can try to make the code/system more like distributed system.
Also we can check for different database connectors with better performance.
Also for missing data instead of writing into rejects.txt file we can write to DB so it would be easier to perform extra analysis.
  
