#!/bin/bash
#
# Start script for confirmation-statement-api
#
PORT=8080

exec java -jar -Dserver.port="${PORT}" "confirmation-statement-api.jar"
