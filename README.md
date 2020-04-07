# Public Announcement Server

Highly Dependable Systems 2019-2020, 2nd semester project

## Installing

- compile and install all modules:

        cd Public-Announcement-Server

        mvn clean install -DskipTests

- generate the keystores and rsa keys

        ./keystore-setup.sh

## Running tests

Note: A server must be running when executing client tests

- how to run servers

        cd pas-server

        (one terminal per server)
        mvn compile exec:java -Dserver.port=xxxx (ex: 9010)

- how to run tests

        cd pas-client

        (in one terminal only)
        mvn verify
