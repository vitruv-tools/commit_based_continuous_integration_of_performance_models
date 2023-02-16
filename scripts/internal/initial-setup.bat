call scripts\internal\check-java-version.bat

:: Initialize submodules
git submodule init
git submodule update

:: Prepare bundles from the CIPM-Pipeline
cd CIPM-Pipeline\cipm.consistency.bridge.eclipse\cipm.consistency.base.shared\dep-generator
call gradlew.bat bundle copyBundles
cd ..\..\..\..
robocopy CIPM-Pipeline\cipm.consistency.bridge.eclipse commit-based-cipm\bundles\fi /E

:: Build JaMoPP
cd Palladio-Supporting-EclipseJavaDevelopmentTools
call mvnw.cmd clean verify -Dmaven.test.skip=true
cd ..

call scripts\internal\start-update-site-server.bat
call scripts\internal\check-update-site-server.bat

:: Build SoMoX
cd Palladio-ReverseEngineering-SoMoX-JaMoPP
git apply --ignore-whitespace ..\scripts\internal\SoMoX-Local-Update-Site-And-Fix.patch
robocopy ..\.mvn .mvn /E
call ..\mvnw.cmd clean verify -Dmaven.test.skip=true
cd ..

call scripts\internal\stop-update-site-server.bat
call scripts\internal\start-update-site-server.bat
call scripts\internal\check-update-site-server.bat

cd Vitruv
robocopy ..\.mvn .mvn /E
call ..\mvnw.cmd clean verify
cd ..

call scripts\internal\stop-update-site-server.bat
