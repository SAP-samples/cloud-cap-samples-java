#!/bin/bash

usage() {
  echo "Usage: $0 [--debug] [--debug-port=PORT] [--debug-suspend=y|n] [--help]"
  echo ""
  echo "Options:"
  echo "  --debug                 Start with remote debug agent"
  echo "  --debug-port=PORT       Debug port to listen on (default: 8000)"
  echo "  --debug-suspend=y|n     Suspend until debugger connects (default: y)"
  echo "  --help                  Show this help message"
}

DEBUG=false
DEBUG_PORT=8000
SUSPEND=y

while [[ $# -gt 0 ]]; do
  case "$1" in
    --debug) DEBUG=true; shift ;;
    --debug-port=*) DEBUG_PORT="${1#*=}"; shift ;;
    --debug-suspend=*) SUSPEND="${1#*=}"; shift ;;
    --help) usage; exit 0 ;;
    *) echo "Unknown option: $1"; usage; exit 1 ;;
  esac
done

DEBUG_ARGS=()
if [[ "$DEBUG" == "true" ]]; then
  echo "Debug mode enabled, listening on port ${DEBUG_PORT} (suspend=${SUSPEND})"
  DEBUG_ARGS=("-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=${SUSPEND},address=*:${DEBUG_PORT}")
fi

npx cds bind bookshop-ai --to ai-core-java
npx cds bind --exec --profile hybrid -- mvn -f srv/pom.xml spring-boot:run -Dspring-boot.run.profiles=default "${DEBUG_ARGS[@]}"
