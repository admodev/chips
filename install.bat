:: This batch script installs chip software using Jpackage in Windows...
ECHO OFF
SET "JAR_PATH=%CD%\app\build\libs"

jpackage ^
  --name chips ^
  --input "%JAR_PATH%" ^
  --main-jar app.jar ^
  --type app-image ^
  --icon jpackage/icon.png ^
  --app-version 1.0.0 ^
  --dest dist
PAUSE
