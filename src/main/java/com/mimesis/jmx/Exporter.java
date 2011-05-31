package com.mimesis.jmx;

import java.io.Closeable;
import java.util.Collection;

abstract public class Exporter implements Closeable{
  public static Exporter newFor(Config cfg) throws Exception {
    Class<?> clazz = cfg.getClazz("exporter.class", Exporter4Cacti.class);
    if (!Exporter.class.isAssignableFrom(clazz)) {
      throw new IllegalArgumentException("value for 'exporter.class' should be a subclass of Exporter :" + clazz.getCanonicalName());
    }
    return (Exporter) clazz.getConstructor(Config.class).newInstance(cfg);
  }

  //public void this(Config config) throws Exception;
  public abstract void export(Collection<Result> data) throws Exception;
  public void close() {};
}
