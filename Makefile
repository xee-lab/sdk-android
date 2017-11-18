.PHONY: help check clean doc

help:
	@echo "----- INSTALL -------------------------------------------------------------------"
	@echo "install              install and link the scripts for the project"
	@echo "----- BUILD ---------------------------------------------------------------------"
	@echo "all                  clean and build the project"
	@echo "build                build the project for release"
	@echo "buildDebug         	build the project for debug"
	@echo "----- LINT -------------------------------------------------------------"
	@echo "lint                 lint all packages"
	@echo "----- OTHERS --------------------------------------------------------------------"
	@echo "clean                clean the project"
	@echo "help                 print this message"
	@echo "doc                  build the javadoc"

all: clean build

install:
	chmod +x ./scripts/pre-commit-config.sh
	ln -sf ../../scripts/pre-commit-config.sh ./.git/hooks/pre-commit
	chmod +x ./.git/hooks/pre-commit

check:
	./gradlew :sdk-core:lint :sdk-api:lint :sdk-fleet:lint

lint:
	./gradlew lint

clean:
	./gradlew clean

build:
	./gradlew :sdk-core:assembleRelease :sdk-api:assembleRelease :sdk-fleet:assembleRelease

buildDebug:
	./gradlew :sdk-core:assembleDebug :sdk-api:assembleDebug :sdk-fleet:assembleDebug

doc:
	./gradlew dokka
