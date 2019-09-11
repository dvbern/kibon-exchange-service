#!/bin/bash

if [ $# != 1 ]
then
  echo "please provide a fully qualified domain name for the certificate"
  exit
fi

openssl req -x509 -nodes -days 3650 -newkey rsa:4096 -keyout key.pem -out cert.pem \
  -subj "/C=CH/ST=Bern/L=Bern/O=DV Berng AG/OU=Web/CN=$1"
