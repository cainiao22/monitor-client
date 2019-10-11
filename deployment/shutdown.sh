#!/bin/sh


export JAVA_HOME=/opt/software/jdk1.8.0_121
export JRE_HOME=$JAVA_HOME/jre
export CATALINA_HOME=/opt/software/apache-tomcat-9.0.4
export CATALINA_BASE="/opt/web/workDir/qddata"

export CATALINA_TMPDIR="$CATALINA_BASE/temp"
export CATALINA_PID="$CATALINA_BASE/tomcat.pid"
sh $CATALINA_HOME/bin/shutdown.sh "$@"
