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

import java.io.Closeable;
import java.util.Collection;

abstract public class Exporter implements Closeable{
  public static Exporter newFor(Config cfg) throws Exception {
    Class<?> clazz = cfg.getClazz("exporter.class", Exporter4CloudwatchAsyncClient.class);
    if (!Exporter.class.isAssignableFrom(clazz)) {
      throw new IllegalArgumentException("value for 'exporter.class' should be a subclass of Exporter :" + clazz.getCanonicalName());
    }
    return (Exporter) clazz.getConstructor(Config.class).newInstance(cfg);
  }

  public abstract void export(Collection<Result> data) throws Exception;
  public void close() {};
}
