package com.mimesis.jmx;

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
