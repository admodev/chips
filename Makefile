all: compile_debian

compile_debian:
	@echo "Compiling chips..."
	jpackage \
  --name chips \
  --input build/ \
  --main-jar chips.jar \
  --main-class chips.App \
  --type deb \
  --icon jpackage/icon.png \
  --app-version 1.0.0 \
  --license-file jpackage/license.txt \
  --dest dist/
