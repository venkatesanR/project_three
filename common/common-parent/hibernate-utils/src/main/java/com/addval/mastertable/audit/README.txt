README for MasterTableAuditor

This simple program try to validate the entries made for constructing master tables for projects.

The validation results could be either fine or error or warning. Some of the decisions as warnings need to be discussed and might be altered as error. Similar is the case for some errors also.

Usage:
-----
It should be run under jdk 1.4 and above only as some of the functions like regexp available only from jdk1.4 

Program is invoked using the main method. Only the Environment class is used by the MasterTableAuditor and so the required external jars are log4j.jar and classes12.zip. 

sample usage:
-------------
java -classpath common.jar;log4j.jar;classes12.zip; com.addval.mastertable.audit.MasterTableAuditor


pre-requirement
----------------
a) change the property file to reflect the data base connection parameters in the mastertable_auditor.properties file in config directory.

b) if required adjust the log file directory and the outputfile directory

On running the program it will create two files in the output directory as Errors.txt and Warnings.txt having details about the errors and warnings encountered during the process.


To do list for version 1.0.
--------------------------

a) output file location is to be configurable.

b) The errors and warnings file should have proper header information to have details like date of run, the DB against which the test is conducted and so on.

c) The layout of the output files is to be mode bit more aesthetic.

d) a batch file is to be provided to run the Application. 



Process of Auditor:
------------------
1) The existence of Editor source name is verified. If the table does not exist it is written in the Errors.txt file under editor errors.

2)If table exists then the errors in columns of editors is verified .if the column does not exists in the source table then the errors are written in the Errors.txt file under Column Errors. If table does not exists then the column errors will not be written in the file.

3)The Warnings for the Columns in the editors are written in Warnings.txt file. If the column data size is entered less then the assigned size for the column then it will be considered as warning and written in the file under the Editor name. The warnings will be written irrelevant of whether the table source exists or not.


4)The column Data Type is checked with the metadata of the column; and it will be considered as warning.

CDT_LONG,CDT_KEY are considered as CDT_INT.
CDT_USER is considered as CDT_STRING.
CDT_DATETIME is considered as CDT_DATE.

5)The column Format is verified .it checks whether the format entered correct with respect to its data type. If it does not match the format then it is written in the Errors file under column errors. And it also checks for the size of the format and the size given in the metadata.

If the size of the entered format is more than the metadata size then it is written in the errors file under column errors.If the format size is less than the metadata size then it is written in warnings file under the editor name.If the format is missing then it is wriiten in the warnings file.

6)The  column Regular Expression is verified.it checks if the reg exp is written in proper format,if the reg exp has any format errors then it will be written in the errors file under the column errors.


7)The Editor SourceSQl is checked.if the table name does not exists ,it is written in the editors error in Errors.txt file.

8)The combo table name is checked,if the column is considered for combo ,then it is checked whether  the combo table name is not present in the editors,if not present then it is witten in column error in the Errors.txt file under the Editor name. 



