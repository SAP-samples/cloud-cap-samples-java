#!/bin/bash

 npx cds bind --exec --profile hybrid -- mvn -f srv/pom.xml spring-boot:run -Dspring-boot.run.profiles=default
