#!/bin/sh

JAR_DIR="`dirname $0`/lib"
ZABBIX_HOSTNAME=$2
JMX_URL=$3
#JMX_URL=127.0.0.1:3333
JMX_USER=$4
JMX_PASS=$5
DATA_FILE=/tmp/zabbix_jmx.${ZABBIX_HOSTNAME}.data
LOG_FILE=/tmp/zabbix_jmx.${ZABBIX_HOSTNAME}.log
MONITOR="MemoryHeap"
MONITOR="${MONITOR}|MemoryNonHeap"
MONITOR="${MONITOR}|Threading"
MONITOR="${MONITOR}|ClassLoading"
#MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=Copy][CollectionCount]"
#MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=Copy][CollectionTime]"
#MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=ConcurrentMarkSweep][CollectionCount]"
#MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=ConcurrentMarkSweep][CollectionTime]"
#MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=MarkSweepCompact][CollectionCount]"
#MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=MarkSweepCompact][CollectionTime]"
#MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=ParNew][CollectionCount]"
#MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=ParNew][CollectionTime]"
MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=PS MarkSweep][CollectionCount]"
MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=PS MarkSweep][CollectionTime]"
MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=PS Scavenge][CollectionCount]"
MONITOR="${MONITOR}|jmx[java.lang:type=GarbageCollector,name=PS Scavenge][CollectionTime]"
#MONITOR="${MONITOR}|jmx[java.lang:type=MemoryPool,name=CMS Old Gen][Usage]"
#MONITOR="${MONITOR}|jmx[java.lang:type=MemoryPool,name=CMS Perm Gen][Usage]"
MONITOR="${MONITOR}|jmx[java.lang:type=MemoryPool,name=Code Cache][Usage]"
#MONITOR="${MONITOR}|jmx[java.lang:type=MemoryPool,name=Perm Gen][Usage]"
MONITOR="${MONITOR}|jmx[java.lang:type=MemoryPool,name=PS Old Gen][Usage]"
MONITOR="${MONITOR}|jmx[java.lang:type=MemoryPool,name=PS Perm Gen][Usage]"
#MONITOR="${MONITOR}|jmx[java.lang:type=MemoryPool,name=Tenured Gen][Usage]"
MONITOR="${MONITOR}|jmx[java.lang:type=Compilation][]"
MONITOR="${MONITOR}|jmx[java.lang:type=OperatingSystem][CpuUsage]"
MONITOR="${MONITOR}|jmx[java.lang:type=OperatingSystem][MaxFileDescriptorCount]"
MONITOR="${MONITOR}|jmx[java.lang:type=OperatingSystem][OpenFileDescriptorCount]"
MONITOR="${MONITOR}|"
echo "args: $*" >${LOG_FILE}
java "-Djava.ext.dirs=${JAR_DIR}" "-Dzabbix.hostname=${ZABBIX_HOSTNAME}" "-Dkey.space.replaceby=_" com.mimesis.jmx.JMXRequester $JMX_URL $JMX_USER $JMX_PASS "${MONITOR}" > ${DATA_FILE} 2>>${LOG_FILE}
BACK=$?
if [ $BACK -eq 0 ] ; then
  zabbix_sender --zabbix-server 127.0.0.1 -T -i ${DATA_FILE} 2>&1 >>${LOG_FILE}
  BACK=$?
fi
echo $BACK
