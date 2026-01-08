@echo off

REM Pour compiler :
javac -encoding UTF-8 -d class @src/compile.list

REM Pour executer :
java -cp class retroconception.Controleur
pause