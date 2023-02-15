:: Check Java version
%JAVA_HOME%\bin\java -version 2>&1|findstr /R "11\.0\."
if %ERRORLEVEL% NEQ 0 (exit /B 1)

:: Initialize submodules
git submodule init
git submodule update

:: Prepare bundles from the CIPM-Pipeline
cd CIPM-Pipeline\cipm.consistency.bridge.eclipse\cipm.consistency.base.shared\dep-generator
call gradlew.bat bundle copyBundles
cd ..\..\..\..
robocopy CIPM-Pipeline\cipm.consistency.bridge.eclipse commit-based-cipm\bundles\fi /E

:: Start server for the update sites of JaMoPP and SoMoX
cd scripts\update-site-server
start cmd /c ..\..\mvnw.cmd spring-boot:run
cd ..\..

:: Build JaMoPP
cd Palladio-Supporting-EclipseJavaDevelopmentTools
call mvnw.cmd clean verify -Dmaven.test.skip=true
cd ..

:: Check that the server for the update site runs
curl --retry 100 --retry-connrefused --retry-delay 1 http://localhost:8081/administration/status
if %ERRORLEVEL% NEQ 0 (exit /B 1)

:: Build SoMoX
cd Palladio-ReverseEngineering-SoMoX-JaMoPP
git apply --ignore-whitespace ..\scripts\internal\SoMoX-Local-Update-Site-And-Fix.patch
robocopy ..\.mvn .mvn /E
call ..\mvnw.cmd clean verify -Dmaven.test.skip=true
cd ..

cd Vitruv
robocopy ..\.mvn .mvn /E
call ..\mvnw.cmd clean verify
cd ..
