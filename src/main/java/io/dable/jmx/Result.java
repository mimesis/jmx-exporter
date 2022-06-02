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

import java.util.Date;

public class Result {
  /**
   * timestamp in epoch (second since 01/01/1970 CET)
   */
  public final long timestampEpoch;
  public final Date timestampDate;
  public final String objectName;
  public final String key;
  public final Object value;
  public final String unit;

  // TODO convert date into UTC
  public Result(Date timestamp, String objectName, String key, Object value, String unit) {
    super();
    this.timestampEpoch = timestamp.getTime() / 1000;
    this.timestampDate = timestamp;
    this.objectName = objectName;
    this.key = key;
    this.value = value;
    this.unit = (unit == null || unit.trim().length() == 0)? null : unit.trim();
  }

}
