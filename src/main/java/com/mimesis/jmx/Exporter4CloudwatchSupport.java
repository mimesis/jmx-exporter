package com.mimesis.jmx;

import java.text.SimpleDateFormat;
/**
 * format for zabbix sender (one line per value)
 *
 * @author david.bernard@mimesis-republic.com
 */
abstract public class Exporter4CloudwatchSupport extends Exporter{

  protected final String _namespace;
  protected final String _dimensions;
  protected final SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  protected final String _cmd;
  protected final String _region;
  protected final String _secretKey;
  protected final String _accessKey;
  protected char _spaceKeyReplacement;

  public Exporter4CloudwatchSupport(Config config) throws Exception {
    _namespace = config.getString("cloudwatch.namespace", null);
    _dimensions = config.getString("cloudwatch.dimensions", null);
    _cmd = config.getString("cloudwatch.cmd", "mon-put-data");
    _region = config.getString("cloudwatch.region", null);
    _secretKey = config.getString("cloudwatch.credential.secretKey", null);
    _accessKey = config.getString("cloudwatch.credential.accessKey", null);
    _spaceKeyReplacement = config.getChar("cloudwatch.key.space.replaceby", '_');
  }

  protected String metricNameOf(String objectName, String key) throws Exception {
    StringBuilder b = new StringBuilder();
    String[] v = objectName.split("=|,");
    for (int i = 0; i <= (v.length - 2); i+= 2) {
      b.append(v[i+1]).append('.');
    }
    b.append(key);
    return b.toString().replace(' ', _spaceKeyReplacement);
  }
}
