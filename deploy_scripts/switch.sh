#!/bin/bash
PROJECT_NAME="github_action"
SWITCH_LOG_PATH="/home/ubuntu/$PROJECT_NAME/switch.log"

echo "> 현재 구동중인 Port 확인" >> SWITCH_LOG_PATH
CURRENT_PROFILE=$(curl -s http://localhost/profile)

if [ $CURRENT_PROFILE == prod1 ]
then
  IDLE_PORT=8081
elif [ $CURRENT_PROFILE == prod2 ]
then
  IDLE_PORT=8080
else
  echo "> 일치하는 Profile이 없습니다. Profile: $CURRENT_PROFILE" >> SWITCH_LOG_PATH
  echo "> 8080을 할당합니다." >> SWITCH_LOG_PATH
  IDLE_PORT=8080
fi

echo "> 전환할 Port: $IDLE_PORT" >> SWITCH_LOG_PATH
echo "> Port 전환" >> SWITCH_LOG_PATH
echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" |sudo tee /etc/nginx/conf.d/service-url.inc

PROXY_PORT=$(curl -s http://localhost/profile)
echo "> Nginx Current Proxy Port: $PROXY_PORT"  >> SWITCH_LOG_PATH

echo "> Nginx Reload" >> SWITCH_LOG_PATH
sudo nginx -s reload
