#!/bin/sh

JAR_DIR="`dirname $0`/lib"
java -Djava.ext.dirs=${JAR_DIR} com.mimesis.jmx.JMXRequester $1 $2 $3 "MemoryHeap|MemoryNonHeap|Threading|ClassLoading|jmx[java.lang:type=GarbageCollector,name=Copy][CollectionCount]|jmx[java.lang:type=MemoryPool,name=Tenured Gen][Usage.used]|jmx[java.lang:type=Compilation][TotalCompilationTime]|jmx[java.lang:type=OperatingSystem][]"  10
