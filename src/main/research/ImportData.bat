SET baseDir=./
SET DB_Driver=com.mysql.jdbc.Driver
SET DB_URL=jdbc:mysql://127.0.0.1/test
SET DB_Username=root
SET DB_Password=""
SET dataFile=/Download/RegistrationServlet 

java -cp ./;./classes;./lib/mysql-connector-java-5.1.7-bin.jar;./lib/log4j.jar;./lib/mail-1.3.jar;c:\CDv2Conf\lib\servlet.jar  ImportData %baseDir% %DB_Driver% %DB_URL% %DB_Username% %DB_Password% %dataFile%

pause