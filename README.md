# robotic-kitchen
> Distrebuted execution system for your robotic kitchen.

Flexible redison based java app for robotic kitchen task managment. 
Stateless java applications for buisness logic execution connects to redis cluster to provide high-level API for your kitchen.
Due to using redison as DB and message delivery system with statless java applications it's easy run on cluster to achieve high availability.

<img src="https://lh3.googleusercontent.com/opXObCx31-bY4SkQiowy-qkPzdVbrC9cKB_zydB8bQqma7DMKkDeutvwCr_3SfWuHT3tXxVzxn57GB8N0iNf5G5eel-Bi_UkfXw_5AzVrsS2L754sXRhPHP6RuFecCyFcjPIkedNtmcEFIkUAr0BM5aa8jSnxjNvCf7OiQzYQn9IrEWkDRkgxXkZOoxiJ4hxSU51oXbGaiMLp4few8y-VSByBNTWCMGf7RrCeOtTFChTQp6l639Vkr4AMsHsXPKpzIBZCrUz5XuXpmR3en54f0k9aaw32wxIVrD_tQF2E9laeTHFnq6vKFri6bQhZKFzL9g3xdARqGm3YfVbQj1vCVkcHDAdR758Y07pVfxHGtfqhazkc2Qi6L6n-SaZkhHfDaFAgGnKkhAaqffTY0TelS0TN1VL_SfCffFaDI8re71J0RBBpfFoz-p74NKVCA4N_ds7_7BoRS0tdOh1wCbCm7hzQoPZOwXRce6w5H17UXdgWnSXZj2h9bwI0QXbvOkajYobr85rt8nA30bTqlQPI02TkdNQHcRwbjV5sbMlo-N9kQWK4ptq2VxgoQK-I9XniSYscjO0u32L1sUpv_68_GqB1tji3re4PZHxKHnGZDjhHcIVbwleuDl4QBzd6XVlTRR3p30O1HJaY7c6kYmHsg9glP_Kxu1KSAnFuzFK0rK1Mt7JCwntdo-7Y14f6bGvSUS7wqjAG46ftM1qWkGTebK77Q=w1229-h1007-no" width="1000">

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
* extract archive somewhere
* add path to extracted files to PATH

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
3. schedule pizza cooking by opening http://localhost:8080/makePizza in browser

Note: you able to make any amount of pizza by one API call by calling same method with optional param "amount" http://localhost:8080/makePizza?amount=10

## Stop
1. stop app
2. stop redis

To stop both use Ctrl+C in according terminal. Note that stop app before redis will lead to more graceful stop.
