# Readme

Base project for a final assignment, contains:
  * deployment code for local environment
  * deployment code for to push to staging
  * scaffolding, interfaces and code snippets

# Prerequisites
  Python 2.7
  docker
  JVM
  Idea

# Docker
  Install - https://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-basics.html
  upgrade aws cli - pip
     `pip install awscli --upgrade --user`
     https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html

## Running Docker Commands as a Non-Root User
The Docker daemon always runs as the root user. The Docker daemon binds to a Unix socket instead of a TCP port. By default, that Unix socket is owned by the user root, and so, by default, you can only access it with sudo. Since we want to be able to package our application as a non-root user, so we need to make sure that sbt-docker can access the socket in non-root. Any Unix user belonging to the group docker can read/write that socket, so you need to add your user to the Docker group.

To add your user (who has root privileges) to the Docker group, run the following command:

sudo usermod -aG docker <username>
newgrp docker

## Management UI
Use
```
docker container run -d -p 9000:9000 -v /var/run/docker.sock:/var/run/docker.sock portainer/portainer
```

## Java base image
docker pull anapsix/alpine-java

# Create topic
Command to create topic named foo with 4 partitions and replication-factor 2
```
docker run --net=host --rm confluentinc/cp-kafka:5.0.0 kafka-topics --create --topic foo --partitions 4 --replication-factor 2 --if-not-exists --zookeeper localhost:32181
```

# Produce/consume topic

You can use bin/kafka-produce TODO
but you can use `kafkacat` tool as well

## consume
```
kafkacat -C -b localhost:19092,localhost:29092,localhost:39092 -t foo -p 0
```

## produce
```
echo 'publish to partition 0' | kafkacat -P -b localhost:19092,localhost:29092,localhost:39092 -t foo -p 0
```

# Components

## solar-panel-emulator

This service is responsible for generating solar plant device data - readings from various kinds of sensors.
In the simplest variant this can be just a bunch of kafka producers. In the provided example scaffolding - this is Akka application.
Speaking of deployment, scaling and location transparency - Akka applications can be built behind principles stated in https://doc.akka.io/docs/akka/2.5/general/remoting.html.
That way you just write your actor hierarchy and where they're going to be deployed is a matter of configuration. Although clustering and remoting mostly is not part of our course you can consider and experiment with these features.
Here are a few directions you can improve this assignment (bonus):
 - actor supervision strategies and random device outages emulation (configurable)
 - add akka nodes and deploy plant actors remotely
 - add akka clustering

You can get inspiration on protocol and some corner cases here - https://doc.akka.io/docs/akka/2.5/guide/tutorial_2.html
Deeper your go - more complex your protocol and application is going to be.

## weather-provider

You should provide implementation for weather message producer

## streaming-app

Kafka Streams application. The main part of the pipeline - joining and enriching two streams of data.

# Build

To build all components into docker images - simply run `sbt docker` from the root project folder.

# Deployment

## Local

To deploy local environment and start testing - simply run `docker-compose up` from the root project folder.

## Staging

TODO