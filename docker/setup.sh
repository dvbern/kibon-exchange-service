#!/bin/bash

function fileExists() {
  if [ ! -f "$1" ]; then
    echo "required file missing: $1"
    exit
  fi
}

fileExists keycloak/kibon_realm.json
fileExists keycloak/set-secrets.sh

if [ $# != 1 ]; then
  echo "please provide a fully qualified domain name for the certificate"
  exit
fi

function createCert() {
  if [ -f "$2/key.pem" ]; then
    echo "$2/key.pem exists"
  elif [ -f "$2/cert.pem" ]; then
    echo "$2/cert.pem exists"
  else
    openssl req -x509 -nodes -days 3650 -newkey rsa:4096 -keyout $2/key.pem -out $2/cert.pem \
      -subj "/C=CH/ST=Bern/L=Bern/O=DV Berng AG/OU=Web/CN=$1"
  fi
}

mkdir -p nginx/certs service/certs kafka/data kafka/jvmlogs zookeeper/data zookeeper/log zookeeper/jvmlogs
createCert $1 nginx/certs
createCert $1 service/certs

echo "Don't forget to update the '.env' file!"
