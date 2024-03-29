#!/bin/sh
cd /Applications/kafka_2.12-2.3.0/bin

# Run with "localhost" so Kafka is available when laptop has no network
MY_HOST=`ipconfig getifaddr en0`
if [ "${MY_HOST}" == "" ]
then
 MY_HOST=localhost
fi
echo advertised.host.name=${MY_HOST}
sleep 3

./kafka-server-start.sh ../config/server.properties \
	--override advertised.host.name=${MY_HOST} \
	--override broker.id=1 \
	--override log.dirs=/tmp/kafka-logs-1 \
	--override port=9093
