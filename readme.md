# Streaming course final project assignment

Base project for a final assignment, contains:
  - deployment code for local environment
  - deployment code for to push to staging (in-progress)
  - scaffolding, interfaces and code snippets

# Environment

## Prerequisites
  - Python 2.7
  - Docker 1.11 or greater, docker-compose
  - JVM 1.8

## Build

To build all of the components - simply run `sbt docker` from the root project folder. This will compile code, build artifacts and push to the local docker repo.

## Deploy

### Local

To deploy local environment and start testing - simply run `docker-compose up` from the root project folder.

### Staging

TODO

## Docker

  We are using Confluent docker images for kafka stack - https://github.com/confluentinc/cp-docker-images.
  
  Control center deployment can be added for visual monitoring kafka cluster but requires additional components - review compose here - https://github.com/confluentinc/cp-docker-images/blob/5.1.0-post/examples/cp-all-in-one/docker-compose.yml


### Running Docker Commands as a Non-Root User

```
sudo usermod -aG docker <username>
newgrp docker
```

### Management UI
Use portainer if you don't want to interact with docker through cli
```
docker container run -d -p 9000:9000 -v /var/run/docker.sock:/var/run/docker.sock portainer/portainer
```

### Java base image
To keep containers lightweight - minimal alpine-linux based image is used for java:
```
docker pull anapsix/alpine-java
```

## Kafka

### Create topic

You can use confluent bundled tools to interact with the cluster, e.g. to create topics:

Command to create topic named foo with 4 partitions and replication-factor 2
```
docker run --net=host --rm confluentinc/cp-kafka:5.0.0 kafka-topics --create --topic foo --partitions 4 --replication-factor 2 --if-not-exists --zookeeper localhost:32181
```

### Produce/consume topic

Or you can use https://github.com/edenhill/kafkacat tool as well, e.g.:

#### consume
```
kafkacat -C -b localhost:19092,localhost:29092,localhost:39092 -t foo -p 0
```

#### produce
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

You can get inspiration on protocol and some corner cases here - https://doc.akka.io/docs/akka/2.5/guide/tutorial_2.html.
Deeper your go - more complex your protocol and application is going to be.

## weather-provider

You should provide implementation for scheduled updates to the weather topic fetching from any freely available weather apis - https://dzone.com/articles/4-free-weather-providers-api-to-develop-weather-ap-1

Or you can use ready-to-use wrapper for scala - https://github.com/snowplow/scala-weather

## streaming-app

Kafka Streams application. The main part of the pipeline - joining and enriching two streams of data. 

You should consider KStream / KTable decision while designing a pipeline.
