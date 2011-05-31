package com.mimesis.jmx;

import static com.mimesis.jmx.Log.logger;

import java.util.Collection;

/**
 * format for zabbix sender (one line per value)
 *
 * @author david.bernard@mimesis-republic.com
 */
public class Exporter4CloudwatchCli extends Exporter4CloudwatchSupport{

  public Exporter4CloudwatchCli(Config config) throws Exception {
    super(config);
  }

  //TODO use the ability to send several data at once or to send 'statisticsValues' instead of 'value'
  public void export(Collection<Result> data) throws Exception {
    for(Result d : data) {
      StringBuilder cmd = new StringBuilder(_cmd);
      if (!(d.value instanceof Number)) {
        logger.info(String.format("CloudWatch only accept value of type Number : [%s][%s] => %s", d.objectName, d.key, String.valueOf(d.value) ));
        continue;
      }
      cmd.append(" --metric-name ").append(metricNameOf(d.objectName, d.key));
      cmd.append(" --value ").append(d.value);
      cmd.append(" --timestamp ").append(_dateFormat.format(d.timestampDate));
      if (d.unit != null) {
        cmd.append(" --unit ").append(d.unit);
      }
      if (_namespace != null) {
        cmd.append(" --namespace ").append(_namespace);
      }
      if (_dimensions != null) {
        cmd.append(" --dimensions ").append(_dimensions);
      }
      if (_region != null) {
        cmd.append(" --region ").append(_region);
      }
      if (_secretKey != null) {
        cmd.append(" --secret-key ").append(_secretKey);
      }
      if (_accessKey != null) {
        cmd.append(" --access-key-id ").append(_accessKey);
      }
      cmd.append('\n');
      System.out.print(cmd.toString());
    }
  }
}
