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

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class Config {
  private final Properties _store = new Properties();

  Config() throws Exception {
  }

  Config(File properties) throws Exception {
    _store.load(new FileReader(properties));
  }

  /**
   * Search a property first in the System properties then in property file
   *
   * @param key
   * @return
   * @throws Exception
   */
  private String getProperty(String key) throws Exception {
    return System.getProperty(key, _store.getProperty(key));
  }

  public String getString(String key, String dft) throws Exception {
    String b = getProperty(key);
    if (b == null || b.trim().length() < 1) {
      return dft;
    }
    return b.trim();
  }

  public char getChar(String key, char dft) throws Exception  {
    String b = getProperty(key);
    if (b == null || b.length() < 1) {
      return dft;
    }
    return b.charAt(0);
  }

  public long getLong(String key, long dft) throws Exception {
    String b = getProperty(key);
    if (b == null || b.length() < 1) {
      return dft;
    }
    return Long.parseLong(b);
  }

  public Class<?> getClazz(String key, Class<?> dft) throws Exception {
    String b = getProperty(key);
    if (b == null || b.length() < 1) {
      return dft;
    }
    return Class.forName(b);
  }
}
