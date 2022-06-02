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

import java.io.IOException;
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
  public void export(Collection<Result> data) throws IOException, InterruptedException {
    for(Result d : data) {
      StringBuilder cmd = new StringBuilder(_cmd);
      if (!(d.value instanceof Number)) {
        Log.logger.info(String.format("CloudWatch only accept value of type Number : [%s][%s] => %s", d.objectName, d.key, d.value));
        continue;
      }
      cmd.append(" --metric-name ").append(metricNameOf(d.objectName, d.key));
      cmd.append(" --value ").append(d.value);
      //cmd.append(" --timestamp ").append(_dateFormat.get().format(d.timestampDate));
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
      String command = cmd.toString();
      Log.logger.info(command);
      Process process = Runtime.getRuntime().exec(command);
      int term = process.waitFor();
      if (term != 0) {
        Log.logger.info("sent metric to cloudwatch failed with %s" + term);
      }
    }
  }
}
