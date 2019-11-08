#!/bin/bash

export CLASSPATH=~/.m2/repository/com/hazelcast/jet/hazelcast-jet/3.2/hazelcast-jet-3.2.jar

ARGS="-Dhazelcast.client.config=src/main/scripts/hazelcast-client.yaml"

BASEDIR=`dirname $0`
cd $BASEDIR/../../..

MODULE=hosebird

PROPERTIES_FILE=~/Documents/application.properties
# No hashes, case-sensitive, two team names ENGMON
HASHTAG="ENGMON"

PARAMS="$PROPERTIES_FILE $HASHTAG"

CMD="java $ARGS com.hazelcast.jet.server.JetCommandLine -v submit ${MODULE}/target/${MODULE}-jar-with-dependencies.jar $PARAMS"
echo $CMD
$CMD
RC=$?
echo RC=$?
exit $RC
