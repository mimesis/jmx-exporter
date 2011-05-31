Tool to export jmx data into file,...
JMX data can come from local JVM or a remote JVM

# Usage

    java -jar jmx-exporter xxx.properties ...

The application export data to stdout (System.out) and log into stderr (System.err), so common usage can be

    java -jar jmx-exporter monitor0.properties 2> /tmp/jmx-monitor0.log 1> /tmp/jmx-monitor0.data

Some sample shell script are provide in the zip archive (jmx-exporter-X.Z-app.zip)

## configuration

Configuration is read from property file (see [sample.properties](sample.properties)).
If there are several configuration files, each configuration will run independently, so several application or several export can be run in a single run.

System properties can define and override any configuration define in property file, they can be used for shared configuration.

The minimal property to define is "jmx.metrics" that list info to retrieve.

## with [CloudWatch]

### with [CloudWatch] command line tool

Configure (in property file) the cloudwatch properties and set exporter `exporter.class=com.mimesis.jmx.Exporter4CloudwatchCli`

Integration with [Zabbix] can be done via the command line tool "mon-put-data".
The tool generate a set of commands (calls mon-put-data) that can be execute after

### with [CloudWatch] java SDK

You need to provide jar from [AWS SDK for java](http://aws.amazon.com/java/) (1.2.0) into the classpath.
Configure (in property file) the cloudwatch properties and set exporter `exporter.class=com.mimesis.jmx.Exporter4CloudwatchAsyncClient`.

## with [Zabbix]

Configure (in property file) the zabbix properties and set exporter `exporter.class=com.mimesis.jmx.Exporter4ZabbixSender`

Integration with [Zabbix] is done via zabbix_sender.
The application generate a data file that can be used as input files for zabbix_sender (1.8.3)
(TODO, document how to create Items into Zabbix for JMX data, and how to configure and to use the template)

## with [Cacti]

The code was originaly build to export data for Cacti (in 2009), but it was not re-tested for Cacti after full refactor (0.2, ...).
Contribution, feedback is hope.

# Alternatives

* CLI
  * [skajla-JMXClient](http://skajla.blogspot.com/2010/05/jmx-command-line-client.html) and a sample [Jboss Monitoring Using Zabbix](http://skajla.blogspot.com/2010/07/jboss-monitoring-using-zabbix.html)
  * [cmdline-jmxclient](http://crawler.archive.org/cmdline-jmxclient/)
* HTTP
  * [Jolokia](http://www.jolokia.org/features-nb.html)
  * [polarrose-jmx-rest-bridge](http://code.google.com/p/polarrose-jmx-rest-bridge/)
* Zabbix
  * [JMX Zabbix Bridge](http://www.kjkoster.org/zapcat/Zapcat_JMX_Zabbix_Bridge.html) (akka Zapcat) for zabbix only, allow to embded in your java code a zabbix agent (require explicit start and to open a other port)
* Collectd
  * [GenericJMX plugin](http://collectd.org/wiki/index.php/Plugin:GenericJMX)

# Why

* Want a bulk request (or at least several request per connection)
* Want to monitor java application without require some modification into their code
* Version 0.1 was created to work with [Catci], then 0.2 to import data into [Zabbix], between 0.1 and this version competitor emerge but I keep it
* I use Java (instead of Scala) to reduce dependencies of the agent, and allow java dev (main JMX user) to contribute.

# TODO

* put Date, timestamp, epach ... in UTC (timezone => should convert from local timezone)
* provide packaging to run as jvm's agent
* notify zabbix users about the project
  * reply into the forum
    * http://www.zabbix.com/forum/showthread.php?t=3988
    * http://www.zabbix.com/forum/showthread.php?t=4673&page=3

# References

* [JMX Home](http://download.oracle.com/javase/6/docs/technotes/guides/jmx/index.html)
* [java.lang.instrument](http://download.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html)

    [CloudWatch]: http://aws.amazon.com/fr/cloudwatch/
    [Cacti]: http://www.cacti.net/
    [Zabbix]: http://www.zabbix.com/