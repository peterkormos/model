SET baseDir=./
SET DB_Driver=oracle.jdbc.driver.OracleDriver
SET DB_URL=jdbc:oracle:thin:@192.168.9.221:1521:baxterdb
SET DB_Username=cdv2_user
SET DB_Password=cdv2_user

java -cp ./classes;./lib/ojdbc14.jar;./lib/log4j.jar;./lib/mail-1.3.jar;c:\CDv2Conf\lib\servlet.jar  DumpToFile %baseDir% %DB_Driver% %DB_URL% %DB_Username% %DB_Password%

pause