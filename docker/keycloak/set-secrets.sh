#/bin/sh
sed -e "s/\${KIBON_EXCHANGE_SERVICE_SECRET}/${KIBON_EXCHANGE_SERVICE_SECRET}/" \
  -e "s/\${KITADMIN_SECRET}/${KITADMIN_SECRET}/" /tmp/kibon_realm.json > /opt/jboss/kibon_realm.json
