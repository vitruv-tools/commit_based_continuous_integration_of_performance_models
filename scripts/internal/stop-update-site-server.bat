:: Stop the server for the local update site
curl http://localhost:8081/administration/stop
if %ERRORLEVEL% NEQ 0 (set ERRORLEVEL=0)
