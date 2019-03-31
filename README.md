# robotic-kitchen
> Distrebuted execution system for your robotic kitchen.

Flexible redison based java app for robotic kitchen task managment. 
Stateless java applications for buisness logic execution connects to redis cluster to provide high-level API for your kitchen.
Due to using redison as DB and message delivery system with statless java applications it's easy run on cluster to achieve high availability.

## Installation

OS X & Linux:

1. install redis
```sh
wget http://download.redis.io/redis-stable.tar.gz
tar xvzf redis-stable.tar.gz
cd redis-stable
make
make install
```
2. install maven 
* download the latest version from the http://maven.apache.org/
* extract somewhere
* add it to PATH

## Run

1. run redis
```sh
redis-server
```
2. run app
```sh
cd path/to/repo/robotic-kitchen
mvn spring-boot:run
```

## Stop
1. stop app
2. stop redis

To stop both use Ctrl+C in according terminal. Note that stop app before redis will lead to more graceful stop.
