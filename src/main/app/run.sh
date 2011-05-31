#!/bin/sh

JAR_DIR="`dirname $0`/lib"
/opt/jdk/bin/java -Djava.ext.dirs=${JAR_DIR} com.mimesis.jmx.JMXRequester $* 
