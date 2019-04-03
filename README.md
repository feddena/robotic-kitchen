# robotic-kitchen
> Distrebuted execution system for your robotic kitchen.

Flexible redison based java app for robotic kitchen task managment. 
Stateless java applications for buisness logic execution connects to redis cluster to provide high-level API for your kitchen.
Due to using redison as DB and message delivery system with statless java applications it's easy run on cluster to achieve high availability.

<img src="https://lh3.googleusercontent.com/1FWA80kEvr04vTFOCFBGkFEHO-tut63i602Ew2MUMsLH6PWdtVQZJ6cXuDA6hyD66LBqdvFiwz9qVcLNNQL1yhPhNFAzJaKuPM96hGOFYIH1e3BW2aASFzlWCNdvLbBpO0dLP0NXOeJd8DZVP5olXQr9Eie0j0bcN9Vt_wHWzr7_YWDOCR7BlgfWt_iQU9lIV-et7JcNKvV80Atvw12HqkYWKE4AvNcs53rzKgFNW9c_5tvoVxmJE_njMkuAJ6j7cCJ1IUqjm5IiQ3bGWyl8eZrBwnlZKv_TxP_9gilcx4H6v8ji05GVM7l5p3EjPnSdYM838biXspqho05OQr-cQWcBzDrqqrDVkxg053ZFhly64b_bh3r7_jekNshbXgtS9vlzbkcPCfmOYgSxAU9s9Tfn2X5cS3aPeZH1Yh9MceDipDCwygH6JwA5zChi8lbQ7kD4Xu_p5c7WGJSQJ5HNfYz7HR9cDszY0H9BkD3EewG7N2EQlG9UlpMNfAE8uKulYRoQgE8UYk4mi6NMy0axjNI5MUI59l59kpT55bkbbpeZYzxn0vuV5Lb1J07UPOrFcC2_luoimRKONAUg7mXaikkWoRk9N5fo9v2H5s0zRoqCg6PTZYmJShQ9qf6RFUh2m6Z5Gh21BbaJa4dUTI6vGuSMDjvNXdq1=w1840-h1848-no" width="1000">

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
