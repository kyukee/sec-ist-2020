#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "necessary arguments: keystore_path alias"
    exit 1
fi

keytool -genkeypair -noprompt \
 -keystore $1 \
 -alias $2 \
 -keyalg RSA \
 -keysize 2048 \
 -storetype pkcs12 \
 -dname "CN=mqttserver.ibm.com, OU=ID, O=IBM, L=Hursley, S=Hants, C=GB" \
 -storepass "password" \
 -keypass "password"
