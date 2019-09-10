#!/bin/bash

MY_CLUSTER_IP=`ifconfig | grep inet | grep -v inet6 | grep -v 127.0.0.1 | cut -d" " -f2`
CMD="docker run -p 8000:8000  -it -e MY_CLUSTER_IP=$MY_CLUSTER_IP fitba/socker-client"
echo $CMD
$CMD
RC=$?
echo RC=$?
exit $RC
