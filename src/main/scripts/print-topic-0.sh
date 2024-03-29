#!/bin/sh
cd /Applications/kafka_2.12-2.3.0/bin

export TOPIC=account
echo =====================
echo Topic name : $TOPIC
echo =====================
 
doIt() {
 CMD="$*"
 echo $CMD
 $CMD
 RC=$?
 echo RC=${RC}
}

TMPFILE=/tmp/`basename ${0}`.$$

doIt ./kafka-topics.sh --zookeeper 127.0.0.1:2181 --list --topic $TOPIC > $TMPFILE 2> /dev/null
cat $TMPFILE

TOPIC_EXISTS=`grep -c ^$TOPIC $TMPFILE`
/bin/rm $TMPFILE

if [ $TOPIC_EXISTS -eq 0 ]
then
 # Replication and partitions for running on a laptop!
 doIt ./kafka-topics.sh --zookeeper 127.0.0.1:2181 --create --partitions 3 --replication-factor 1 --topic $TOPIC
 # Allow leadership election
 /bin/echo Waiting....
 sleep 5
 echo Done.
fi

doIt ./kafka-topics.sh --zookeeper 127.0.0.1:2181 --describe --topic $TOPIC > $TMPFILE 2> /dev/null
cat $TMPFILE
/bin/rm $TMPFILE

echo \^C to cancel...
doIt ./kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic $TOPIC --from-beginning
