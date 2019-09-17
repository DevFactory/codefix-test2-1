#!/bin/bash

aws s3api get-object --bucket codefix-config --key $ENVIRONMENT/credentials.sh /app/credentials.sh
source /app/credentials.sh

java $JAVA_OPTS -jar /app/codefix.jar
