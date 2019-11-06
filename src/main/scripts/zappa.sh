#!/bin/bash

export CLASSPATH=~/.m2/repository/com/hazelcast/jet/hazelcast-jet/3.2/hazelcast-jet-3.2.jar

ARGS="-Dhazelcast.client.config=src/main/scripts/hazelcast-client.yaml"

BASEDIR=`dirname $0`
cd $BASEDIR/../../..

MODULE=zappa

CMD="java $ARGS com.hazelcast.jet.server.JetCommandLine -v submit ${MODULE}/target/${MODULE}-jar-with-dependencies.jar"
echo $CMD
$CMD
RC=$?
echo RC=$?
exit $RC
