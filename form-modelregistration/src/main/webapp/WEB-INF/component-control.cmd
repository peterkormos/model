@echo off
setlocal

if "%1" == "start" goto startApp
"%~f0" start %*
goto end

:startApp
shift /1

set COMPID=${project.artifactId}
set COMPNAME=${project.name}

set SCRIPT_DIR=%~dp0
set PE_HOME=%SCRIPT_DIR%..

set JAR_PATH=%PE_HOME%\lib
set BIN_PATH=%PE_HOME%\bin
set LIB_PATH=%JAR_PATH%\native
set CONF_PATH=%BIN_PATH%\conf
set LOG_PATH=%PE_HOME%\log

if not exist "%CONF_PATH%" mkdir "%CONF_PATH%" 

set START_TARGET=${start.target}

rem Set JAVA_HOME to JRE instead of JDK
if exist "%JAVA_HOME%\bin\javac.exe" set JAVA_HOME=%JAVA_HOME%\jre

rem Call Environment script if exists
rem Environment script will set two variables: 
rem   REST and VARIANTS
set PE_ENV=%BIN_PATH%\pe-env.cmd
if exist "%PE_ENV%" call "%PE_ENV%"

rem Define request URL parts
set URL_VERSION=version=${requiredConfigVersion}
set URL_VARIANTS=${requiredConfigVariant}
if not "%VARIANTS%" == "" set URL_VARIANTS=%URL_VARIANTS%,%VARIANTS%

rem Retrieve JAVA_OPTS script and execute it
set JAVA_OPTS_CMD=%CONF_PATH%\vm_%COMPID%.cmd
if exist "%JAVA_OPTS_CMD%" del /F /Q "%JAVA_OPTS_CMD%"
set JAVA_OPTS_VBS=%JAVA_OPTS_CMD%.vbs
if exist "%JAVA_OPTS_VBS%" del /F /Q "%JAVA_OPTS_VBS%"
set JAVA_OPTS_URL=%REST%/${project.groupId}/%COMPID%/%URL_VARIANTS%/jvm?%URL_VERSION%
echo WGETting %JAVA_OPTS_URL% to %JAVA_OPTS_CMD%
echo ' The content is generated > "%JAVA_OPTS_VBS%"
echo strFileURL = "%JAVA_OPTS_URL%&osfamily=windows" >> "%JAVA_OPTS_VBS%"
echo strHDLocation = "%JAVA_OPTS_CMD%" >> "%JAVA_OPTS_VBS%"
echo Set objXMLHTTP = CreateObject("MSXML2.XMLHTTP") >> "%JAVA_OPTS_VBS%"
echo objXMLHTTP.open "GET", strFileURL, false >> "%JAVA_OPTS_VBS%"
echo objXMLHTTP.send() >> "%JAVA_OPTS_VBS%"
echo If objXMLHTTP.Status = 200 Then >> "%JAVA_OPTS_VBS%"
echo     Set objADOStream = CreateObject("ADODB.Stream") >> "%JAVA_OPTS_VBS%"
echo     objADOStream.Open >> "%JAVA_OPTS_VBS%"
echo     objADOStream.Type = 1 'adTypeBinary >> "%JAVA_OPTS_VBS%"
echo     objADOStream.Write objXMLHTTP.ResponseBody >> "%JAVA_OPTS_VBS%"
echo     objADOStream.Position = 0    'Set the stream position to the start >> "%JAVA_OPTS_VBS%"
echo     Set objFSO = Createobject("Scripting.FileSystemObject") >> "%JAVA_OPTS_VBS%"
echo     If objFSO.Fileexists(strHDLocation) Then objFSO.DeleteFile strHDLocation >> "%JAVA_OPTS_VBS%"
echo     Set objFSO = Nothing >> "%JAVA_OPTS_VBS%"
echo     objADOStream.SaveToFile strHDLocation >> "%JAVA_OPTS_VBS%"
echo     objADOStream.Close >> "%JAVA_OPTS_VBS%"
echo     Set objADOStream = Nothing >> "%JAVA_OPTS_VBS%"
echo End if >> "%JAVA_OPTS_VBS%"
echo Set objXMLHTTP = Nothing >> "%JAVA_OPTS_VBS%"
call cscript.exe "%JAVA_OPTS_VBS%"
if not exist "%JAVA_OPTS_CMD%" goto error
call "%JAVA_OPTS_CMD%" %1 %2 %3 %4 %5

set PATH=%JAVA_HOME%\bin;%LIB_PATH%;%PATH%
set LAUNCH_CMD="%JAVA_HOME%\bin\javaw.exe" %JAVA_OPTS% %START_TARGET% %1 %2 %3 %4 %5
rem Starting separately
set LAUNCH_CMD=start "%COMPNAME%" %LAUNCH_CMD%

echo ----------------------------------------------
echo About to launch %COMPNAME%
echo ----------------------------------------------
echo JAVA_HOME=%JAVA_HOME%
echo ----------------------------------------------
echo JAVA_OPTS=%JAVA_OPTS%
echo ----------------------------------------------
echo CLASSPATH=%CLASSPATH%
echo ----------------------------------------------
echo PATH=%PATH%
echo ----------------------------------------------
echo Command:
echo %LAUNCH_CMD%
echo ----------------------------------------------

%LAUNCH_CMD%

goto end

:error
echo Failed to start application

:end
endlocal