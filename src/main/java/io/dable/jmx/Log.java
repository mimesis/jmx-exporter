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

public class Log {
  public final static Log logger = new Log();

  private final static long t0 = System.currentTimeMillis();
  //private static Log _logger = new Log();

  public void info(String msg) {
    System.err.println("INFO " + (System.currentTimeMillis() - t0) + " " + msg);
  }

  public void warn(Throwable t) {
    System.err.println("WARN " + (System.currentTimeMillis() - t0) + " " + t.getClass().getCanonicalName() + ":" + t.getMessage());
    t.printStackTrace(System.err);
  }
}
