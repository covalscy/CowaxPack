@echo off
echo ========================================
echo Building and Deploying CowaxPack
echo ========================================

echo.
echo Building project...
call gradlew build --no-daemon

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Build failed! Check the errors above.
    pause
    exit /b 1
)

echo.
echo Build completed successfully!
echo.

set FORGE_JAR="build\libs\cowaxpack-1.1.0.jar"
set TARGET1="E:\Games inst\UltimMC\instances\battlecrafttest\.minecraft\mods"

echo Copying mod to target directories...

if exist %FORGE_JAR% (
    echo Copying mod to: %TARGET1%
    copy /Y %FORGE_JAR% %TARGET1%
    
    echo Mod copied successfully!
    echo.
) else (
    echo.
    echo ERROR: Mod JAR file not found at: %FORGE_JAR%
    echo Please check the build output and file name.
    echo.
    echo Available JAR files in build\libs\:
    dir "build\libs\*.jar" /b 2>nul
    echo.
)

echo.
echo ========================================
echo Deployment completed successfully!
echo ========================================
echo.
echo Mods copied to:
echo - %TARGET1%
echo.

echo Verifying installation...
echo.
echo Files in %TARGET1%:
dir "%TARGET1%\cowaxpack*" /b 2>nul
echo.

pause