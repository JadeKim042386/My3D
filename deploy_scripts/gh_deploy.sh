#!/bin/bash
PROJECT_NAME="github_action"
JAR_PATH="/home/ubuntu/github_action/build/libs/*.jar"
DEPLOY_PATH="/home/ubuntu/$PROJECT_NAME/"
SWITCH_LOG_PATH="/home/ubuntu/$PROJECT_NAME/switch.log"
DEPLOY_LOG_PATH="/home/ubuntu/$PROJECT_NAME/deploy.log"
DEPLOY_ERR_LOG_PATH="/home/ubuntu/$PROJECT_NAME/deploy_err.log"
APPLICATION_LOG_PATH="/home/ubuntu/$PROJECT_NAME/application.log"
BUILD_JAR=$(ls $JAR_PATH)
JAR_NAME=$(basename $BUILD_JAR)
STATIC_PATH="/home/ubuntu/$PROJECT_NAME/src/main/resources/static"

echo "===== 배포 시작 : $(date +%c) =====" >> $DEPLOY_LOG_PATH

echo "> build 파일명: $JAR_NAME" >> $DEPLOY_LOG_PATH
echo "> build 파일 복사" >> $DEPLOY_LOG_PATH
cp $BUILD_JAR $DEPLOY_PATH

echo "> 현재 활성화된 Profile 확인" >> $DEPLOY_LOG_PATH
CURRENT_PROFILE=$(curl -s http://localhost/profile)
echo "> $CURRENT_PROFILE" >> $DEPLOY_LOG_PATH

echo "> 현재 활성화된 Profile이 아닌 Profile을 선택" >> $DEPLOY_LOG_PATH
if [ $CURRENT_PROFILE == prod1 ]
then
  IDLE_PROFILE=prod2
  IDLE_PORT=8081
elif [ $CURRENT_PROFILE == prod2 ]
then
  IDLE_PROFILE=prod1
  IDLE_PORT=8080
else
  echo "> 일치하는 Profile이 없습니다. Profile: $CURRENT_PROFILE" >> $DEPLOY_LOG_PATH
  echo "> prod1을 할당합니다. IDLE_PROFILE: prod1" >> $DEPLOY_LOG_PATH
  IDLE_PROFILE=prod1
  IDLE_PORT=8080
fi

# -- Change jar
echo "> application.jar 교체" >> $DEPLOY_LOG_PATH
IDLE_APPLICATION=$IDLE_PROFILE-my3d.jar
IDLE_APPLICATION_PATH=$DEPLOY_PATH$IDLE_APPLICATION

ln -Tfs $DEPLOY_PATH$JAR_NAME $IDLE_APPLICATION_PATH

# -- Current Application Kill
echo "> $CURRENT_PROFILE 에서 구동중인 애플리케이션 pid 확인" >> $DEPLOY_LOG_PATH
CURRENT_PID=$(pgrep -f $CURRENT_PROFILE-my3d.jar)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> $DEPLOY_LOG_PATH
else
  echo "> sudo kill -15 $CURRENT_PID" >> $DEPLOY_LOG_PATH
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

# -- Switching
echo "> 전환할 Port: $IDLE_PORT" >> SWITCH_LOG_PATH
echo "> Port 전환" >> SWITCH_LOG_PATH
echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" |sudo tee /etc/nginx/conf.d/service-url.inc

PROXY_PORT=$(curl -s http://localhost/profile)
echo "> Nginx Current Proxy Port: $PROXY_PORT"  >> SWITCH_LOG_PATH

echo "> Nginx Reload" >> SWITCH_LOG_PATH
sudo nginx -s reload

# -- Deploy
echo "> $IDLE_PROFILE 배포" >> $DEPLOY_LOG_PATH
nohup java -jar -Dspring.profiles.active=$IDLE_PROFILE $IDLE_APPLICATION_PATH >> $APPLICATION_LOG_PATH 2> $DEPLOY_ERR_LOG_PATH &

echo "> static 경로 저장" >> $DEPLOY_LOG_PATH
echo "set \$static_path ${STATIC_PATH};" |sudo tee /etc/nginx/conf.d/static-path.inc

sleep 3

echo "> 배포 종료 : $(date +%c)" >> $DEPLOY_LOG_PATH
