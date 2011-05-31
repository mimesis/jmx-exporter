package com.mimesis.jmx;

public class Log {
  public final static Log logger = new Log();

  private final static long t0 = System.currentTimeMillis();
  //private static Log _logger = new Log();

  public void info(String msg) {
    System.err.println("INFO " + (System.currentTimeMillis() - t0) + " " + msg);
  }

  public void warn(Throwable t) {
    System.err.println("WARN " + (System.currentTimeMillis() - t0) + " " + t.getClass().getCanonicalName() + ":" + t.getMessage());
    t.printStackTrace(System.err);
  }
}
