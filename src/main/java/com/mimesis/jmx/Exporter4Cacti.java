package com.mimesis.jmx;

import java.util.Collection;

/**
 * format for Cacti JTG every data on a single line
 *
 * @author david.bernard@mimesis-republic.com
 */
public class Exporter4Cacti extends Exporter{

  public Exporter4Cacti(Config config) throws Exception {
  }

  // TODO manage objectName
  public void export(Collection<Result> data) throws Exception {
    for (Result d : data) {
      System.out.format("%s: %s ", d.key, String.valueOf(d.value));
    }
    System.out.println();
  }

}
