#!/bin/bash

CLIENTS_KEYSTORE="pas-client/src/test/resources/client-keystore.jks"
SERVERS_KEYSTORE="pas-server/src/test/resources/server-keystore.jks"

if [ "$#" -ne 1 ]; then
    echo "necessary arguments: keystore_path"
    echo ""
    echo "maybe you wanted one of these options?"
    echo "clients: " $CLIENTS_KEYSTORE
    echo "servers: " $SERVERS_KEYSTORE
    exit 1
fi

keytool -list -storetype JKS -keystore $1 -storepass password
