# robotic-kitchen
> Distrebuted execution system for your robotic kitchen.

Flexible redison based java app for robotic kitchen task managment. 
Stateless java applications for buisness logic execution connects to redis cluster to provide high-level API for your kitchen.
Due to using redison as DB and message delivery system with statless java applications it's easy run on cluster to achieve high availability.

<img src="https://lh3.googleusercontent.com/uPE7rYobJ-nKEqr9COH_J23IUZ8oZFE98AXso71AdjTod4_YfWPuIixIDxg2jqBJXUljJm_4ojpFHl0GzVZBzu2f0qwSkNzSUexRBas1V1H65LUNAsuOoxhAZz2IYqJ9gNPugAzGEWUvSavkt2Ek085OkNgVjmbTQVHS4fOtD-roqY67iBO_VhiPAat_cB4Mrspn0SVVTHL3hJNOvHs8OptKDc4JQz7EOxGiviir9EpPd5ucUWBuO0p0zMoUMhVRHN8qqOglrMsC1_XvENFZWT_EUuq4JSDdwJ9dos2dgfQDw8r5efSil6HZuze0Mo3OVnbXNxdoRStgVQeU8V_aQkEhha0hi2JBZUj5Nz2zu1bYygTlvnUEtj3TFRnY7AzKNgHZihBR-adSyPiKRXM6-qADqr7Dl1Uox9fgfwqCPD3wroEW_7-iDUF1-h0gpwPLIiR9Yjcys3hyS-seFzntQvBf9qDYuwp3T3Wt8FgmgaY2RI3zbXbVSBnY9Fe_ewIWce-zwQBMkyUdt8otMybKCtK80J3GmDhBlrwuC5g6OXnBimdQ0TjWrrkDZ4BLs0PmhgChjMwwGTW_9WdwY6PA0Hjo4o4nOzGGtb1ktgOPYfOTI3k3mOZWPoqrX6xm2yn_bH6gcSetYS-t1xlZogVEQtnmTIGCNF8f=w1614-h1672-no" width="1000">

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

Note: 
* you able to make any amount of a pizza by one API call by calling the same method with optional param "amount" http://localhost:8080/makePizza?amount=10
* to see what happens in more detail change level of logs in the ```log4j.properties``` file and restart the app 

## Stop
1. stop app
2. stop redis

To stop both use Ctrl+C in according terminal. Note that stop app before redis will lead to more graceful stop.
