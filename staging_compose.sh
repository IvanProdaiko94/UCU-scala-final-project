#!/usr/bin/env bash

set -e  # exit immediately if a command exits with a non-zero status
set -x  # print all executed commands on terminal

export $(grep -v '^#' .env | xargs)

ecs-cli compose --cluster-config ucu-class --region us-east-1 --debug --project-name $STUDENT_NAME-streaming-ucu-final-project service "$@"