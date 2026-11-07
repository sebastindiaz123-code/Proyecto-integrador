@echo off
setlocal

set "PROJECT_DIR=%~dp0"
set "APP_DIR=%PROJECT_DIR%GrabadoresPro_Portable_Actualizado\GrabadoresPro"
set "MAVEN_LOCAL=%PROJECT_DIR%.maven\apache-maven-3.9.6\bin\mvn.cmd"

if not exist "%APP_DIR%\GrabadoresPro.exe" (
    echo [ERROR] No se encontro GrabadoresPro.exe en el paquete portable.
    pause
    exit /b 1
)

echo Compilando la version actual...
where mvn >nul 2>nul
if errorlevel 1 (
    if not exist "%MAVEN_LOCAL%" (
        echo [ERROR] No se encontro Maven. Instala Maven o revisa la carpeta .maven.
        pause
        exit /b 1
    )
    call "%MAVEN_LOCAL%" -q -f "%PROJECT_DIR%pom.xml" package
) else (
    call mvn -q -f "%PROJECT_DIR%pom.xml" package
)

if errorlevel 1 (
    echo [ERROR] No se pudo compilar. La aplicacion no se ha actualizado.
    pause
    exit /b 1
)

if not exist "%PROJECT_DIR%target\GrabadoresApp-1.0.0.jar" (
    echo [ERROR] No se encontro el JAR compilado.
    pause
    exit /b 1
)

copy /Y "%PROJECT_DIR%target\GrabadoresApp-1.0.0.jar" "%APP_DIR%\app\GrabadoresApp-1.0.0.jar" >nul
if errorlevel 1 (
    echo [ERROR] No se pudo actualizar el JAR de la aplicacion.
    pause
    exit /b 1
)

echo Cambios aplicados. Abriendo Grabadores Pro...
start "" /D "%APP_DIR%" "%APP_DIR%\GrabadoresPro.exe"
exit /b 0
