openapi-lint:
	docker run --rm -v ${PWD}:/app stoplight/spectral lint \
		/app/rest-api/src/main/resources/openapi.yaml \
		--ruleset /app/rest-api/.spectral.yml

openapi-run:
	docker run --rm -p 8000:8080 -e SWAGGER_JSON=/app/rest-api/src/main/resources/openapi.yaml \
		-v ${PWD}:/app swaggerapi/swagger-ui

docker-build:
	docker build -t accreditation-app .

docker-run: docker-build
	docker run -p 8080:8080 --name accreditation-app accreditation-app
