#!/bin/bash

usage() {
  echo "Usage: $0 [--debug] [--debug-port=PORT] [--help]"
  echo ""
  echo "Options:"
  echo "  --debug             Start with remote debug agent (suspends until debugger connects)"
  echo "  --debug-port=PORT   Debug port to listen on (default: 8000)"
  echo "  --help              Show this help message"
}

DEBUG=false
DEBUG_PORT=8000

while [[ $# -gt 0 ]]; do
  case "$1" in
    --debug) DEBUG=true; shift ;;
    --debug-port=*) DEBUG_PORT="${1#*=}"; shift ;;
    --help) usage; exit 0 ;;
    *) echo "Unknown option: $1"; usage; exit 1 ;;
  esac
done

DEBUG_ARGS=""
if [[ "$DEBUG" == "true" ]]; then
  echo "Debug mode enabled, listening on port ${DEBUG_PORT}"
  DEBUG_ARGS="-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:${DEBUG_PORT}"
fi

npx cds bind bookshop-ai --to ai-core-java
npx cds bind --exec --profile hybrid -- mvn -f srv/pom.xml spring-boot:run -Dspring-boot.run.profiles=default "${DEBUG_ARGS}"
