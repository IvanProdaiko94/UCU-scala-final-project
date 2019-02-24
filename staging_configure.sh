#!/usr/bin/env bash

set -e  # exit immediately if a command exits with a non-zero status
set -x  # print all executed commands on terminal

export $(grep -v '^#' .env | xargs)

AWS_ACCESS_KEY_ID=$1
AWS_SECRET_ACCESS_KEY=$2

ecs-cli --version

ecs-cli configure --config-name ucu-class --cluster ucu-class --default-launch-type FARGATE --region us-east-1 --compose-service-name-prefix service-
ecs-cli configure default --config-name ucu-class
ecs-cli configure profile --access-key $AWS_ACCESS_KEY_ID --secret-key $AWS_SECRET_ACCESS_KEY --profile-name ucu-class
ecs-cli configure profile default --profile-name ucu-class

# login to ecr registry
$(aws ecr get-login --region us-east-1 --no-include-email)

