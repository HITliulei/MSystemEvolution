#!/bin/bash

mkdir -p /data/test-dir/log

docker run -d -v /data/test-dir/log:/var/log/mclient \
    --env MCLIENT_LOG_DIR_PATH=/var/log/mclient \
    --env MCLIENT_LOGSTASH_IP=144.34.200.189 \
    --env MCLIENT_LOGSTASH_PORT=32001 \
    --env MCLIENT_CADVISOR_IP=144.34.200.189 \
    --env MCLIENT_CADVISOR_PORT=4042 \
    septemberhx/minfo-collector-service:v1.0.7
