@ECHO OFF
SET MAVEN_WRAPPER_DIR=%~dp0\.mvn\wrapper
SET WRAPPER_JAR=%MAVEN_WRAPPER_DIR%\maven-wrapper.jar
SET DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
IF NOT EXIST "%WRAPPER_JAR%" (
  echo Downloading Maven Wrapper JAR...
  powershell -Command "Invoke-WebRequest -OutFile '%WRAPPER_JAR%' '%DOWNLOAD_URL%'" || (
    echo Failed to download Maven Wrapper
    exit /b 1
  )
)
REM Pass multi-module project directory property so the wrapper knows the project base dir
REM Append a dot to %~dp0 to avoid a trailing backslash before the quote which can break Windows argument parsing
java -Dmaven.multiModuleProjectDirectory="%~dp0." -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
