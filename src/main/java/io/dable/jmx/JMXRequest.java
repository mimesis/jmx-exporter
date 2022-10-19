/**
 * Copyright (C) 2011 Mimesis-Republic <http://mimesis-republic.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dable.jmx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JMXRequest {
  protected String objectName = null;
  protected ItemInfo[] wantedVariables = EMPTY;
  protected ItemInfo[] wantedOperations = EMPTY;
  protected boolean enable = true;

  static class ItemInfo {
    public ItemInfo(String name, String unit, String dft) {
      super();
      this.name = name;
      this.unit = (unit == null || unit.trim().length() == 0)? null : unit.trim();
      this.dft = (dft == null || dft.trim().length() == 0)? null : dft.trim();
    }
    public ItemInfo(String name) {
      this(name, null, null);
    }
    public final String name;
    public final String unit;
    public final String dft;

  }
  //---------------------------------------------------------------------------
  //  FACTORY

  private static Pattern _jmxPattern = Pattern.compile("\\[([^\\]]*)\\]\\[([^\\]]*)\\](\\[([^\\]]*)\\](\\[([^\\]]*)\\])?)?");
  private static ItemInfo[] EMPTY = new ItemInfo[] {};

  public static JMXRequest[] newFor(Config cfg) throws Exception {
    return newFor(cfg.getString("jmx.metrics", "").split("\\|"));
  }

  public static JMXRequest[] newFor(String[] infos) throws Exception {
    JMXRequest[] back = new JMXRequest[infos.length];
    for (int i = infos.length-1; i > -1; i--) {
      String info = infos[i].trim();
      if (info.length() < 1) continue;
      String[] parts = info.split("#");
      if(parts.length > 1) {
        back[i] = newFor(parts[0], parts[1]);
      } else {
        back[i] = newFor(parts[0], "");
      }
    }
    return groupByObjectName(back);
  }

  public static JMXRequest newFor(String label, String param1) {
    //System.err.println("-->> " + label + " # " + param1);
    JMXRequest back = new JMXRequest();
    Matcher m = null;
    if ("MemoryHeap".equals(label)) {
      back.objectName = "java.lang:type=Memory";
      back.wantedVariables = new ItemInfo[]{new ItemInfo("HeapMemoryUsage")};
    } else if ("MemoryNonHeap".equals(label)){
      back.objectName = "java.lang:type=Memory";
      back.wantedVariables = new ItemInfo[]{new ItemInfo("NonHeapMemoryUsage")};
    } else if ("Threading".equals(label)) {
      back.objectName = "java.lang:type=Threading";
      back.wantedVariables = new ItemInfo [] {
        new ItemInfo("DaemonThreadCount"),
        new ItemInfo("PeakThreadCount"),
        //"CurrentThreadCpuTime", "CurrentThreadUserTime",
        new ItemInfo("ThreadCount"),
        new ItemInfo("TotalStartedThreadCount")
      };
    } else if ("ClassLoading".equals(label)) {
      back.objectName = "java.lang:type=ClassLoading";
      back.wantedVariables = new ItemInfo[]{new ItemInfo("LoadedClassCount"), new ItemInfo("UnloadedClassCount"), new ItemInfo("TotalLoadedClassCount")};
    } else if ("MinaStatistics".equals(label)) {
      back.objectName = "org.apache.mina:type=IoServiceStatistics";
      if (param1 != null && param1.length() != 0) {
        back.objectName = back.objectName + ",name=" + param1;
      }
      back.wantedVariables = new ItemInfo [] {
        new ItemInfo("cumulativeManagedSessionCount"),
        new ItemInfo("largestManagedSessionCount"),
        new ItemInfo("largestReadBytesThroughput"),
        new ItemInfo("largestReadMessagesThroughput"),
        new ItemInfo("largestWrittenBytesThroughput"),
        new ItemInfo("largestWrittenMessagesThroughput"),
        new ItemInfo("readBytes"),
        new ItemInfo("readMessages"),
        new ItemInfo("writtenBytes"),
        new ItemInfo("writtenMessages")
      };
    } else if ("MinaSession".equals(label)) {
      back.objectName = "org.apache.mina:type=IoSessionGauge";
      if (param1 != null && param1.length() != 0) {
        back.objectName = back.objectName + ",name=" + param1;
      }
      back.wantedVariables = new ItemInfo [] { new ItemInfo("sessionGauge") };
    } else if ("MemoryPool".equals(label)){
      back.objectName = param1;
      back.wantedVariables = new ItemInfo[]{new ItemInfo("Usage")};
    } else if ("Compilation".equals(label)) {
      back.objectName = "java.lang:type=Compilation";
      back.wantedVariables = new ItemInfo [] { new ItemInfo("TotalCompilationTime") };
    } else if ("OperatingSystem".equals(label)) {
      back.objectName = "java.lang:type=OperatingSystem";
      back.wantedVariables = new ItemInfo[] {
        new ItemInfo("MaxFileDescriptorCount"),
        new ItemInfo("OpenFileDescriptorCount"),
        new ItemInfo("CommittedVirtualMemorySize"),
        new ItemInfo("FreePhysicalMemorySize"),
        new ItemInfo("FreeSwapSpaceSize"),
        new ItemInfo("ProcessCpuTime"),
        new ItemInfo("TotalPhysicalMemorySize"),
        new ItemInfo("TotalSwapSpaceSize"),
        new ItemInfo("AvailableProcessors"),
        new ItemInfo("SystemLoadAverage"),
        new ItemInfo("ProcessCpuLoad"),
        new ItemInfo("SystemCpuLoad"),
      };
    } else if ((m = _jmxPattern.matcher(label)).matches()) {
      back.objectName = m.group(1);
      back.wantedVariables = new ItemInfo[] {new ItemInfo(m.group(2), m.group(4), m.group(6))};
    }

    if (back.objectName == null) {
      throw new IllegalArgumentException("no rules to create JmxRequest for '" + label + "'");
    }
    return back;
  }

  private static JMXRequest[] groupByObjectName(JMXRequest[] reqs) throws Exception {
    HashMap<String, JMXRequest> m = new HashMap<String, JMXRequest>();
    for(JMXRequest req : reqs) {
      if (req.objectName == null) continue;
      JMXRequest existing = m.get(req.objectName);
      JMXRequest newReq = null;
      if (existing == null) {
        newReq = req;
      } else {
        newReq = new JMXRequest();
        newReq.objectName = existing.objectName;
        newReq.wantedOperations = concat(existing.wantedOperations, req.wantedOperations);
        newReq.wantedVariables =  concat(existing.wantedVariables, req.wantedVariables);
      }
      m.put(newReq.objectName, newReq);
    }
    ArrayList<JMXRequest> back = new ArrayList<JMXRequest>(m.values());
    Collections.sort(back, (o1, o2) -> o1.objectName.compareTo(o2.objectName));
    return back.toArray(new JMXRequest[back.size()]);
  }

  private static ItemInfo[] concat(ItemInfo[] a1, ItemInfo[] a2) throws Exception {
    ItemInfo[] back = new ItemInfo[a1.length + a2.length];
    System.arraycopy(a1, 0, back, 0, a1.length);
    System.arraycopy(a2, 0, back, a1.length, a2.length);
    return back;
  }
}
