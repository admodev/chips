all: compile_debian

compile_debian:
	@echo "Compiling chips..."
	jpackage \
		--name chips \
  	--input app/build/libs \
  	--main-jar app.jar \
  	--main-class chips.App \
  	--type deb \
  	--app-version 1.0.0 \
  	--license-file jpackage/license.txt \
  	--dest dist/
