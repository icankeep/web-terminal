DOCKER_REPO ?= icankeep/web-terminal
COMMIT_ID := $(shell git log -1 --pretty=%h)
COMMIT_TAG := $(shell git describe --tags ${COMMIT_ID})
COMMIT := $(shell if [[ -z "$$(git status --porcelain)" ]]; then ${COMMIT_ID}; else echo ${COMMIT_ID}-dirty; fi)
VERSION := $(shell if [[ -n "${COMMIT_TAG}" ]]; then echo "${COMMIT_TAG}"; else echo "beta"; fi)
TAG := ${VERSION}.${COMMIT}

$(warning "==============================")
$(warning "ARG INFO")
$(warning "COMMIT_ID: ${COMMIT_ID}")
$(warning "COMMIT_TAG: ${COMMIT_TAG}")
$(warning "COMMIT: ${COMMIT}")
$(warning "VERSION: ${VERSION}")
$(warning "TAG: ${TAG}")
$(warning "==============================")

$(warning "Build Starting...")

version:
	git tag ${version}
	git push origin ${version}

build:
	./mvnw clean package -Dmaven.test.skip=true
	docker build --tag ${DOCKER_REPO}:${TAG} --build-arg JAR_FILE=target/web-terminal-*.jar .

push:
	docker push ${DOCKER_REPO}:${TAG}

tag:
	docker tag ${DOCKER_REPO}:${TAG} ${DOCKER_REPO}:latest
	docker push ${DOCKER_REPO}:latest

release: build push tag