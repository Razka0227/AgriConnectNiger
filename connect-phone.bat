@echo off
REM AgriConnect - connecte le telephone (USB) au backend du PC
REM A relancer a chaque rebranchement du telephone
setlocal
set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"

if not exist "%ADB%" (
  echo ADB introuvable: %ADB%
  exit /b 1
)

echo [1/3] Demarrage du serveur adb...
"%ADB%" start-server >nul

echo [2/3] Tunnel USB: localhost:8080 telephone  -^>  8080 PC
"%ADB%" reverse tcp:8080 tcp:8080

echo [3/3] Verification...
"%ADB%" shell curl -s -m 6 -o /dev/null -w "Backend joignable: HTTP=%%{http_code}\n" http://localhost:8080/api/products

echo.
echo Vous pouvez ouvrir l'app AgriConnect sur le telephone.
pause
