mvn_install:
	mvn clean install -DskipTests
docker_build:
	docker build -t solviadscom/cmis-service:0.1.2 .
docker_run:
	docker run -p 9091:9091 --name=cmis-test solviadscom/cmis-service:0.1.2
docker_push:
	docker push solviadscom/cmis-service:0.1.2
deploy_kyma:
	kubectl apply -f deploymentcmis.yaml
