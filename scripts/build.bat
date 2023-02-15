:: Prepare the repository
call scripts\internal\initial-setup.bat

:: Build the actual pipeline
cd commit-based-cipm
..\mvnw.cmd clean verify -P run-one
..\mvnw.cmd clean verify -P run-two
cd ..

:: Resource cleaning
call scripts\internal\stop-update-site-server.bat
