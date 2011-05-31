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
