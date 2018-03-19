setlocal

set ANT_HOME=C:\jakarta-ant-1.5.1
set JAVA_HOME=C:\bea\jdk141_05

set path=%JAVA_HOME%\bin;%ANT_HOME%\bin

set COMMON_HOME=C:\Projects\COMMON
set COMMON_HOME1.4=C:\Projects\COMMON1.4\SRC
set CLASSPATH=%COMMON_HOME%\lib\config;%COMMON_HOME%\lib\ext\commons-beanutils.jar;%COMMON_HOME%\lib\ext\commons-collections.jar;%COMMON_HOME%\lib\ext\commons-digester.jar;%COMMON_HOME%\lib\ext\commons-logging.jar;%COMMON_HOME%\lib\ext\xerces.jar;%COMMON_HOME%\lib\ext\xercesImpl.jar;%COMMON_HOME%\lib\ext\xmlParserAPIs.jar;%COMMON_HOME1.4%\lib\classes

java com.addval.dbutils.DAOSQLLoaderTest DAORules.xml TextDAO.xml

