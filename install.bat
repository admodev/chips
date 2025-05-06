:: This batch script installs chip software using Jpackage in Windows...
ECHO OFF
jpackage \
  --name chips \
  --input build/ \
  --main-jar chips.jar \
  --type exe \
  --icon jpackage/icon.png \
  --app-version 1.0.0 \
  --license-file jpackage/license.txt \
  --dest dist/
PAUSE
