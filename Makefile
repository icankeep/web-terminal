DOCKER_REPO ?= icankeep/web-terminal
VERSION ?= 0.0.1.beta1
COMMIT := $(shell if [[ -z "$$(git status --porcelain)" ]]; then git log -1 --pretty=%h; else git log -1 --pretty=%h-dirty-$$RANDOM; fi)
TAG := ${VERSION}.${COMMIT}

build:
	./mvnw clean package -Dmaven.test.skip=true
	docker build --tag ${DOCKER_REPO}:${TAG} --build-arg JAR_FILE=target/web-terminal-*.jar .

push:
	docker push ${DOCKER_REPO}:${TAG}

release: build push