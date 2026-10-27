#!/bin/bash

DEBUG_ARGS=""
if [[ "$1" == "--debug" ]]; then
  DEBUG_ARGS="-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"
fi

npx cds bind bookshop-ai --to ai-core-java
npx cds bind --exec --profile hybrid -- mvn -f srv/pom.xml spring-boot:run -Dspring-boot.run.profiles=default $DEBUG_ARGS
