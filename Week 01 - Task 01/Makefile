# Makefile

# Set the default target
.DEFAULT_GOAL := help

# Set the output directory and determine the operating system
ifeq ($(OS),Windows_NT)
	OUTPUT_DIR := $(USERPROFILE)\Downloads
else
	OUTPUT_DIR := $(HOME)/Downloads
endif

.PHONY: build
build:
	@docker build --no-cache -t da-spring-app .

.PHONY: run
run:
	@docker run -d -p 80:8080 --name da-spring-app da-spring-app

.PHONY: exec
exec:
	@docker exec -it da-spring-app /bin/bash

.PHONY: stop
stop:
	@docker stop da-spring-app

.PHONY: clean
clean:
	@docker stop da-spring-app
	@docker rm da-spring-app

.PHONY: prune
prune:
	@docker image rm da-spring-app

.PHONY: deploy
deploy:
	@docker build --no-cache -t da-spring-app .
	@docker save -o $(OUTPUT_DIR)/da-spring-app.tar da-spring-app
	@echo Make sure Docker is installed on the server.
	@echo Copy the TAR file ($(OUTPUT_DIR)/da-spring-app.tar) and the .env file to the server.
	@echo Run the following commands on the server to load and run the Docker container:
	@echo 1. docker load -i da-spring-app.tar
	@echo 2. docker run -d -p 80:8080 --env-file .env --name da-spring-app da-spring-app
	@echo The application should be available under the IP address of the server on port 80.

.PHONY: help
help:
	@echo "Available targets:"
	@echo "  build      - Build the Docker image"
	@echo "  run        - Run the Docker container"
	@echo "  exec       - Enter the Docker container"
	@echo "  stop       - Stop the Docker container"
	@echo "  clean      - Stop and remove the Docker container"
	@echo "  prune      - Prune the image"
	@echo "  deploy     - Get instructions on how to deploy the application"
	@echo "  help       - Show this help message"
