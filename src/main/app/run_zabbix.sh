#!/bin/sh
#
# Copyright (C) 2011 Mimesis-Republic <http://mimesis-republic.com/>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


JAR_DIR="`dirname $0`/lib"
java -Djava.ext.dirs=${JAR_DIR} com.mimesis.jmx.JMXRequester $1 $2 $3 "MemoryHeap|MemoryNonHeap|Threading|ClassLoading|jmx[java.lang:type=GarbageCollector,name=Copy][CollectionCount]|jmx[java.lang:type=MemoryPool,name=Tenured Gen][Usage.used]|jmx[java.lang:type=Compilation][TotalCompilationTime]|jmx[java.lang:type=OperatingSystem][]"  10
