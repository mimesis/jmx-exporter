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

import java.util.Collection;

/**
 * format for zabbix sender (one line per value)
 *
 * @author david.bernard@mimesis-republic.com
 */
public class Exporter4ZabbixSender extends Exporter{

  private final char _spaceKeyReplacement;
  private final String _hostname;
  private final String _format;

  public Exporter4ZabbixSender(Config config) throws Exception {
    _spaceKeyReplacement = config.getChar("zabbix.key.space.replaceby", '_');
    _hostname = config.getString("zabbix.hostname", "-");
    _format = config.getString("zabbix.format", "%s jmx[%s][%s] %d %s\n");
  }

  public void export(Collection<Result> data) throws Exception {
    for(Result d : data) {
      String fkey = d.key.replace(' ', _spaceKeyReplacement);
      String fobjectName = d.objectName.replace(' ', _spaceKeyReplacement);
      System.out.format(_format, _hostname, fobjectName, fkey, d.timestampEpoch, d.value);
    }
  }
}
