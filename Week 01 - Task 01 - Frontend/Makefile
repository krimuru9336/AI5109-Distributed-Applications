 # author: Lucas Immanuel Nickel
 # matriculation: 1318441
 # date: 2023-11-19

.DEFAULT_GOAL := help

ifeq ($(OS),Windows_NT)
	OUTPUT_DIR := $(USERPROFILE)/Downloads
else
	OUTPUT_DIR := $(HOME)/Downloads
endif

DOCKER = docker
DOCKER_BUILD = $(DOCKER) build
DOCKER_RUN = $(DOCKER) run
DOCKER_EXEC = $(DOCKER) exec
DOCKER_STOP = $(DOCKER) stop
DOCKER_RM = $(DOCKER) rm
DOCKER_IMAGE_RM = $(DOCKER) image rm
DOCKER_SAVE = $(DOCKER) save

APP_IMAGE = frontend-nginx-image
APP_CONTAINER = frontend-nginx-container
EGRESS_PORT = 80

.PHONY: build
build:
	$(DOCKER_BUILD) -t $(APP_IMAGE) .

.PHONY: run
run:
	$(DOCKER_RUN) -d -p $(EGRESS_PORT):80 \
		--name $(APP_CONTAINER) \
		$(APP_IMAGE)

.PHONY: exec
exec:
	$(DOCKER_EXEC) -it $(APP_CONTAINER) /bin/bash

.PHONY: stop
stop:
	$(DOCKER_STOP) $(APP_CONTAINER)

.PHONY: clean
clean:
	$(DOCKER_STOP) $(APP_CONTAINER)
	$(DOCKER_RM) $(APP_CONTAINER)

.PHONY: prune
prune:
	$(DOCKER_IMAGE_RM) $(APP_IMAGE)

.PHONY: deploy
deploy: build
	$(DOCKER_SAVE) -o $(OUTPUT_DIR)/$(APP_IMAGE).tar $(APP_IMAGE)
	@echo Make sure Docker is installed on the server.
	@echo Copy the TAR file ($(OUTPUT_DIR)/$(APP_IMAGE).tar) to the server.
	@echo Run the following commands on the server to load and run the Docker container:
	@echo 1. docker load -i $(APP_IMAGE).tar
	@echo 2. $(DOCKER_RUN) -d -p $(EGRESS_PORT):80 --name $(APP_CONTAINER) $(APP_IMAGE)
	@echo The application should be available under the IP address of the server on port $(EGRESS_PORT).

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