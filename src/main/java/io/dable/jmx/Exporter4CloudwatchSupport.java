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

import java.text.SimpleDateFormat;
/**
 * format for cloud watch (one line per value)
 *
 * @author david.bernard@mimesis-republic.com
 */
abstract public class Exporter4CloudwatchSupport extends Exporter{

  protected final String _namespace;
  protected final String _dimensions;
  protected final static ThreadLocal<SimpleDateFormat> _dateFormat = ThreadLocal.withInitial(
      () -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
  protected final String _cmd;
  protected final String _region;
  protected final String _secretKey;
  protected final String _accessKey;
  protected char _spaceKeyReplacement;

  public Exporter4CloudwatchSupport(Config config) throws Exception {
    _namespace = config.getString("cloudwatch.namespace", null);
    _dimensions = config.getString("cloudwatch.dimensions", null);
    _cmd = config.getString("cloudwatch.cmd", "aws cloudwatch put-metric-data");
    _region = config.getString("cloudwatch.region", "ap-northeast-2");
    _secretKey = config.getString("cloudwatch.credential.secretKey", null);
    _accessKey = config.getString("cloudwatch.credential.accessKey", null);
    _spaceKeyReplacement = config.getChar("cloudwatch.key.space.replaceby", '_');
  }

  protected String metricNameOf(String objectName, String key) {
    StringBuilder b = new StringBuilder();
    String[] v = objectName.split("=|,");
    for (int i = 0; i <= (v.length - 2); i+= 2) {
      b.append(v[i+1]).append('.');
    }
    b.append(key);
    return b.toString().replace(' ', _spaceKeyReplacement);
  }
}
