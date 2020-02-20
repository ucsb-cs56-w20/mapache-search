@echo off 

set SPRING_APPLICATION_JSON={

FOR /F "skip=1 delims=" %%a in (localhost.json) do (
  call set "SPRING_APPLICATION_JSON=%%SPRING_APPLICATION_JSON%%%%a"
)