:: This batch script installs chip software using Jpackage in Windows...
ECHO OFF
jpackage ^
  --name chips ^
  --input app/build/libs ^
  --main-jar app.jar ^
  --type app-image ^
  --icon jpackage/icon.png ^
  --app-version 1.0.0 ^
  --dest dist
PAUSE
