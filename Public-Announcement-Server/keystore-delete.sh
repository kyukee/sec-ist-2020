#!/bin/bash

# use this to setup the demonstration environment

CLIENTS_KEYSTORE="pas-client/src/main/resources/client-keystore.jks"
SERVERS_KEYSTORE="pas-server/src/main/resources/server-keystore.jks"

rm $CLIENTS_KEYSTORE
rm $SERVERS_KEYSTORE
