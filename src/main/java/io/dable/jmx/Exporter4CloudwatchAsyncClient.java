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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Future;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;

/**
 * Exporter that send data directly to cloudwatch, so nothing is printed on stdout
 *
 * @author david.bernard@mimesis-republic.com
 */
public class Exporter4CloudwatchAsyncClient extends Exporter4CloudwatchSupport {

  private static final int AWS_METRICS_MAX = 20;
  private final AWSCredentials _awsCredentials;
  private Future<Void> _lastFuture = null;

  public Exporter4CloudwatchAsyncClient(Config config) throws Exception {
    super(config);
    _awsCredentials = new BasicAWSCredentials(config.getString("cloudwatch.credential.accessKey", null), config.getString("cloudwatch.credential.secretKey", null));
  }

  //TODO use the ability to send several data at once or to send 'statisticsValues' instead of 'value'
  @Override
  public void export(Collection<Result> data) throws Exception {
    //TODO configure client to use POST
    AmazonCloudWatchAsyncClient awsClient = new AmazonCloudWatchAsyncClient(_awsCredentials);
    if (_region != null) {
      awsClient.setEndpoint(String.format("monitoring.%s.amazonaws.com", _region));
    }

    Collection<Dimension> dimensions = parseDimensions(_dimensions);
    ArrayList<MetricDatum> metrics = new ArrayList<MetricDatum>(AWS_METRICS_MAX);
    System.out.println("send START");
    int i = 0;
    for(Result d : data) {
      if (!(d.value instanceof Number)) {
        Log.logger.info(String.format("CloudWatch only accept value of type Number : [%s][%s] => %s", d.objectName, d.key, String.valueOf(d.value) ));
        continue;
      }
      MetricDatum metricDatum = new MetricDatum()
        .withMetricName(metricNameOf(d.objectName, d.key))
        .withValue(((Number)d.value).doubleValue())
        .withTimestamp(d.timestampDate)
        ;
      if (dimensions.size() > 0) {
        metricDatum = metricDatum.withDimensions(dimensions);
      }
      if (d.unit != null) {
        metricDatum = metricDatum.withUnit(d.unit);
      }
      System.out.println("send : " + (++i) + metricDatum.getMetricName());
      metrics.add(metricDatum);
      if (metrics.size() == AWS_METRICS_MAX ) {
        waitEndOfLastExport();
        _lastFuture = awsClient.putMetricDataAsync(newPutMetricDataRequest(metrics));
        metrics = new ArrayList<MetricDatum>(AWS_METRICS_MAX);
      }
    }
    if (metrics.size() > 0) {
      waitEndOfLastExport();
      _lastFuture = awsClient.putMetricDataAsync(newPutMetricDataRequest(metrics));
    }
  }

  private PutMetricDataRequest newPutMetricDataRequest(ArrayList<MetricDatum> metrics) {
    PutMetricDataRequest putMetricDataRequest = new PutMetricDataRequest();
    if (_namespace != null) {
      putMetricDataRequest = putMetricDataRequest.withNamespace(_namespace);
    }
    return putMetricDataRequest.withMetricData(metrics);
  }

  /**
   * @param v dimensions as String in "key1=value1,key2=value2..." (null return empty collection)
   */
  private Collection<Dimension> parseDimensions(String v) throws Exception {
    ArrayList<Dimension> back = new ArrayList<Dimension>();
    if (v != null) {
      String[] pairs = v.split(",");
      if (pairs != null) {
        for(String s : pairs) {
          int pos = s.indexOf("=");
          if (pos > 0) {
            Dimension d = new Dimension().withName(s.substring(0, pos)).withValue(s.substring(pos+1));
            back.add(d);
          }
        }
      }
    }
    return back;
  }

  private void waitEndOfLastExport() throws Exception {
    // block until last export is not done
    if (_lastFuture != null) {
      _lastFuture.get();
      _lastFuture = null;
    }
  }

  @Override
  public void close() {
    try {
      waitEndOfLastExport();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      Log.logger.warn(e);
    }
    super.close();
  }
}
