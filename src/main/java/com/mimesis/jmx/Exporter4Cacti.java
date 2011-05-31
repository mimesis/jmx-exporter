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
