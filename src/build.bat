:: Windows build script for msweephex
::
@echo off

:: Help option
if %1.==help. (
  echo Usage: build [arg]
  echo No arg to build bytecode and jar
  echo Single arg 'clean' to clean up built files 
  echo Single arg 'help' for this message
  exit /b 
)


if %1.==clean. goto clean 

:build

:: Make the manifest
echo Main-Class: msweep.Msweep>.\manifest.txt
:: Compile .java files to .class files
echo Building bytecode
for /r %%F in (*.java) do (
  javac %%F
) 

:: Make dist folder
mkdir ..\dist
:: Make jar distribution
echo Building jar
jar cmf .\manifest.txt ..\dist\MsweepHex.jar .\*
echo Done build
exit /b

:clean
:: Remove manifest
echo Removing generated manifest
del /Q  .\manifest.txt
:: Remove class files
echo Removing bytecode
del /Q /S *.class
:: Remove dist
echo Removing jar dist
rmdir /Q /S ..\dist
echo Done clean
exit /b

