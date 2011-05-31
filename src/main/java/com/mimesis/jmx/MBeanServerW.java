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
package com.mimesis.jmx;

import static com.mimesis.jmx.Log.logger;

import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public abstract class MBeanServerW implements Closeable {
  static MBeanServerW newFor(Config cfg) throws Exception {
    String url = cfg.getString("jmx.url", null);
    MBeanServerW back = null;
    if (url != null) {
      back = new MBeanServerW4Remote(cfg);
    } else {
      back = new MBeanServerW4Local();
    }
    return back;
  }

  abstract MBeanServerConnection mbs() throws Exception;
  public void close(){}
}

class MBeanServerW4Local extends MBeanServerW {
  MBeanServerConnection mbs() throws Exception {
    return ManagementFactory.getPlatformMBeanServer();
  }
}

class MBeanServerW4Remote extends MBeanServerW {
  private JMXConnector _connector;

  public MBeanServerW4Remote(Config cfg) throws Exception {
    String url = cfg.getString("jmx.url", null);
    if (url != null && !url.startsWith("service:jmx")) {
      url = "service:jmx:rmi:///jndi/rmi://" + url + "/jmxrmi";
    }
    // System.err.println("-- " + jmxURL);
    String user = cfg.getString("jmx.user", null);
    String pass = cfg.getString("jmx.pwd", null);
    JMXServiceURL jmxUrl = new JMXServiceURL(url);
    Map<String, Object> m = new HashMap<String, Object>();
    m.put(JMXConnector.CREDENTIALS, new String[] { user, pass });
    logger.info("JMXConnector...");
     _connector = JMXConnectorFactory.connect(jmxUrl, m);
    logger.info("...JMXConnector DONE");
  }

  MBeanServerConnection mbs() throws Exception {
    logger.info("MBeanServerConnection...");
    MBeanServerConnection back = _connector.getMBeanServerConnection();
    logger.info("...MBeanServerConnection DONE");
    return back;
  }

  public void close(){
    if (_connector != null) {
      JMXConnector c = _connector;
      _connector = null;
      try {
        c.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
