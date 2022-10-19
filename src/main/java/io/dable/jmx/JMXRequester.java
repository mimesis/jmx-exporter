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

import static io.dable.jmx.Log.logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

//TODO provide a daemon mode
//TODO use jmx' notification listener in daemon mode
//TODO in daemon mode provide an info about connected or not (eg : output uptime to zero)

public class JMXRequester {
  private final MBeanServerW _mbsw;
  private final Exporter _exporter;

  public JMXRequester(Config cfg) throws Exception {
    _mbsw = MBeanServerW.newFor(cfg);
    _exporter = Exporter.newFor(cfg);
  }

  public void output(JMXRequest[] reqs) throws Exception {
    try {
      MBeanServerConnection connection = _mbsw.mbs();
      Collection<Result> data = collect(reqs, connection);
      _exporter.export(data);
    } finally {
      // close
      _mbsw.close();
      _exporter.close();
    }
  }

  public Collection<Result> collect(JMXRequest[] reqs, MBeanServerConnection connection) throws Exception {
    Date timestamp = new Date();//System.currentTimeMillis();
    ArrayList<Result> back = new ArrayList<Result>();
    for (JMXRequest req : reqs) {
      if (! req.enable) continue;
      try {
        // locate object
        ObjectName on = new ObjectName(req.objectName);

        // operations
        for (JMXRequest.ItemInfo op : req.wantedOperations) {
          Object result = connection.invoke(on, op.name, new Object[] {}, new String[] {});
          back.add(new Result(timestamp, req.objectName, op.name, result, op.unit));
        }

        // attributes
        JMXRequest.ItemInfo[] attributes = req.wantedVariables;
        if (attributes == null || attributes.length == 0) {
          MBeanInfo info = connection.getMBeanInfo(on);
          MBeanAttributeInfo[] mais = info.getAttributes();
          attributes = new JMXRequest.ItemInfo[mais.length];
          for (int i = mais.length - 1; i > -1; i--) {
            attributes[i] = new JMXRequest.ItemInfo(mais[i].getName());
          }
        }
        String[] attributesName = mapToName(attributes);
        AttributeList al = connection.getAttributes(on, attributesName);
        for (Attribute var : al.asList()) {
          Object attr2 = var.getValue();
          if (attr2 instanceof CompositeDataSupport) {
            CompositeDataSupport cds2 = (CompositeDataSupport) attr2;
            Set<String> keys = cds2.getCompositeType().keySet();
            for (String key : keys) {
              back.add(new Result(timestamp, req.objectName, var.getName() + "." + key, cds2.get(key), findUnit(var.getName(), attributes)));
            }
          } else {
            back.add(new Result(timestamp, req.objectName, var.getName(), attr2, findUnit(var.getName(), attributes)));
          }
        }
        //HACK
        if ("java.lang:type=OperatingSystem".equals(req.objectName)){
          CpuUsageSample sample = getCpuUsage(connection);
          back.add(new Result(timestamp, "java.lang:type=OperatingSystem", "ProcessCpuTime", sample.processCpuTime, "Milliseconds"));
          back.add(new Result(timestamp, "java.lang:type=OperatingSystem", "ProcessCpuLoad", sample.processCpuLoad, "Percent"));
          back.add(new Result(timestamp, "java.lang:type=OperatingSystem", "SystemCpuLoad", sample.systemCpuLoad, "Percent"));
        }
      } catch (javax.management.InstanceNotFoundException exc) {
        //exc.printStackTrace();
        logger.info("NOT found : " + exc.getMessage());
        req.enable = false;
      }
    }
    return back;
  }

  private String findUnit(String name, JMXRequest.ItemInfo[] attributes) {
    for (int i = attributes.length - 1; i > -1; i--) {
      if (attributes[i].name.equals(name)) {
        return attributes[i].unit;
      }
    }
    return null;
  }

  private String[] mapToName(JMXRequest.ItemInfo[] attributes) {
    String[] attributesName = new String[attributes.length];
    for (int i = attributes.length - 1; i > -1; i--) {
      attributesName[i] = attributes[i].name;
    }
    return attributesName;
  }

  private static class CpuUsageSample {
    long processCpuTime; // https://docs.oracle.com/en/java/javase/11/docs/api/jdk.management/com/sun/management/OperatingSystemMXBean.html#getProcessCpuTime()
    double systemCpuLoad; // https://docs.oracle.com/en/java/javase/11/docs/api/jdk.management/com/sun/management/OperatingSystemMXBean.html#getSystemCpuLoad()
    double processCpuLoad; // https://docs.oracle.com/en/java/javase/11/docs/api/jdk.management/com/sun/management/OperatingSystemMXBean.html#getProcessCpuLoad()
  }

  private static double roundPercent(double value) {
    BigDecimal bd = new BigDecimal(Double.toString(value));
    bd = bd.setScale(2, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  private CpuUsageSample getCpuUsage(MBeanServerConnection connection) {
    CpuUsageSample sample = new CpuUsageSample();
    try {
      ObjectName osObjectName = new ObjectName("java.lang:type=OperatingSystem");
      sample.processCpuTime = (Long)connection.getAttribute(osObjectName, "ProcessCpuTime");
      sample.processCpuTime = TimeUnit.NANOSECONDS.toMillis(sample.processCpuTime);
      sample.processCpuLoad = (Double) connection.getAttribute(osObjectName, "ProcessCpuLoad");
      sample.processCpuLoad = roundPercent(sample.processCpuLoad * 100.0d);
      sample.systemCpuLoad = (Double) connection.getAttribute(osObjectName, "SystemCpuLoad");
      sample.systemCpuLoad = roundPercent(sample.systemCpuLoad * 100.0d);
    } catch (Exception ignored) {
      // ignored
    }
    return sample;
  }
}
