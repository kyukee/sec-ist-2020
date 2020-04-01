#!/bin/bash

# use this to setup the demonstration environment

CLIENTS_KEYSTORE="pas-client/src/main/resources/keystore.jks"
SERVERS_KEYSTORE="pas-server/src/main/resources/keystore.jks"

rm $CLIENTS_KEYSTORE
rm $SERVERS_KEYSTORE

./keystore-generate.sh $CLIENTS_KEYSTORE client1
./keystore-generate.sh $CLIENTS_KEYSTORE client2
./keystore-generate.sh $CLIENTS_KEYSTORE client3

./keystore-generate.sh $SERVERS_KEYSTORE server1
