#!/bin/bash

mvn clean package
kubectl delete service eureka-server
kubectl delete deployment eureka-server
docker rmi micheallei/meurekaserver:v2.0
docker rmi meurekaserver:v2.0
docker build -t meurekaserver:v2.0 .
docker tag meurekaserver:v2.0 micheallei/meurekaserver:v2.0
docker push micheallei/meurekaserver:v2.0
kubectl create -f eureka-server.yaml