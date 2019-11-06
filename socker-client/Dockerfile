FROM adoptopenjdk/openjdk11:jre-11.0.5_10-alpine

# Copy from Maven build output
ARG JAR_FILE
ADD target/${JAR_FILE} application.jar

ENV MY_CLUSTER_IP ""

ENTRYPOINT exec java -DMY_CLUSTER_IP=$MY_CLUSTER_IP -jar application.jar
