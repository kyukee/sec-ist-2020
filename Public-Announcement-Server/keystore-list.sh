#!/bin/bash

CLIENTS_KEYSTORE="pas-client/src/main/resources/keystore.jks"
SERVERS_KEYSTORE="pas-server/src/main/resources/keystore.jks"

if [ "$#" -ne 1 ]; then
    echo "necessary arguments: keystore_path"
    echo ""
    echo "maybe you wanted one of these options?"
    echo "clients: " $CLIENTS_KEYSTORE
    echo "servers: " $SERVERS_KEYSTORE
    exit 1
fi

keytool -list -storetype JKS -keystore $1 -storepass password
