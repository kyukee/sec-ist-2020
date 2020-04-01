# Public Announcement Server

Highly Dependable Systems 2019-2020, 2nd semester project



### Installing

- To compile and install all modules:

cd to Public-Announcement-Server

mvn clean install -DskipTests


### Running tests

- how to run servers

cd to pas-server

one terminal per server: mvn compile exec:java -Dserver.port=xxxx (ex: 9010)


- how to run tests

cd to pas-client

in one terminal only: mvn verify
