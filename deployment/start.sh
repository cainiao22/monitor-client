#!/bin/bash

export JAVA_HOME=/opt/software/jdk1.8.0_121
export JRE_HOME=$JAVA_HOME/jre
export CATALINA_HOME=/opt/software/apache-tomcat-9.0.4
export CATALINA_BASE="/opt/web/workDir/qddata"
export CATALINA_TMPDIR="$CATALINA_BASE/temp"
export CATALINA_PID="$CATALINA_BASE/tomcat.pid"
export JAVA_OPTS="-Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.rmi.port=6969 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=10.50.8.55 -server -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$CATALINA_BASE/logs/heapdump.hprofjvm  -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=512M  -Xms4096m -Xmx4096m  -XX:CMSInitiatingOccupancyFraction=70 -XX:NewRatio=1 -Xloggc:$CATALINA_BASE/logs/gc.log -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Djava.awt.headless=true -Dtomcat.name=qddata  -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintTenuringDistribution"
#cp $CATALINA_BASE/logging.properties $CATALINA_BASE/webapps/
#创建logs目录
if [ ! -d "$CATALINA_BASE/logs" ]; then
  mkdir $CATALINA_BASE/logs
fi

#创建temp目录
if [ ! -d "$CATALINA_BASE/temp" ]; then
  mkdir $CATALINA_BASE/temp
fi

rm -rf $CATALINA_BASE/work/Catalina/localhost/*

# 调用tomcat启动
sh  $CATALINA_HOME/bin/startup.sh "$@"
