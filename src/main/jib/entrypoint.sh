#!/bin/sh

echo "The application will startFileMonitor in ${JHIPSTER_SLEEP}s..." && sleep ${JHIPSTER_SLEEP}
exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "it.unimib.disco.bigtwine.NerApp"  "$@"
