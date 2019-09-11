#!/bin/bash

export CLASSPATH=~/.m2/repository/com/hazelcast/jet/hazelcast-jet/3.1/hazelcast-jet-3.1.jar

ARGS="-Dhazelcast.client.config=src/main/scripts/hazelcast-client.yaml"

BASEDIR=`dirname $0`
cd $BASEDIR/../../..

CMD="java $ARGS com.hazelcast.jet.server.JetCommandLine -v submit cat-scan/target/cat-scan-jar-with-dependencies.jar"
echo $CMD
$CMD
RC=$?
echo RC=$?
exit $RC
