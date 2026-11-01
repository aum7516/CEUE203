@echo off
set "JAVA_BIN=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\java.exe"
if exist "%JAVA_BIN%" (
    "%JAVA_BIN%" -jar minibank.jar
) else (
    java -jar minibank.jar
)
pause
