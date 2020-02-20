@echo off 

set SPRING_APPLICATION_JSON={

rem *** Two empty lines are required for the linefeed
FOR /F "skip=1 delims=" %%a in (localhost.json) do (
  call set "SPRING_APPLICATION_JSON=%%SPRING_APPLICATION_JSON%%%%a"
)

echo %SPRING_APPLICATION_JSON%
