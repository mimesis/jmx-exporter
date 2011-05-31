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

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

/**
 * To use as java's agent, need to be declare in META-INF/MANIFEST.MF with
 *
 * <pre>
 * Agent-Class: com.mimesis.jmx.Main
 * </pre>
 *
 * @see http://download.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html
 */
public class Main {

  public static void agentmain(String args) {
    try {
      logger.info("start jmx-exporter as agent");
      start(configurations(args.split(",")), Long.getLong("jmx.loop.sleep", 0));
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  // This the new method you need
  public static void agentmain(String agentArgs, Instrumentation inst) {
    agentmain(agentArgs);
  }

  public static void main(String[] args) throws Exception {
    int exitCode = start(configurations(args), Long.getLong("jmx.loop.sleep", 0));
    System.exit(exitCode);
  }

  private static int start(List<Config> configs, long sleepTime) throws Exception {
    int exitCode = 0;
    if (configs.size() < 1) {
      logger.info("usage : Main configfile ...");
      exitCode = -1;
    } else {
      do {
        exitCode = run(configs);
        if (sleepTime > 0) {
          logger.info("sleeping " + (sleepTime / 1000) + "s before next run...");
          Thread.sleep(sleepTime);
        }
      } while(exitCode == 0 && sleepTime > 0);
    }
    return exitCode;
  }

  private static int run(List<Config> configs) throws Exception {
    int exitCode = 0;
    for (Config config : configs) {
      try {
        JMXRequester requester = new JMXRequester(config);
        requester.output(JMXRequest.newFor(config));
      } catch (Exception exc) {
        logger.warn(exc);
        exitCode = -2;
      }
    }
    return exitCode;
  }

  private static List<Config> configurations(String[] args) throws Exception {
    ArrayList<Config> back = new ArrayList<Config>();
    if (args.length < 1) {
      if (System.getProperty("jmx.metrics") != null) {
        back.add(new Config());
      }
    } else {
      for (String arg : args) {
        back.add(new Config(new File(arg)));
      }
    }
    return back;
  }
}
