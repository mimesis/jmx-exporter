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

Configure (in property file) the cloudwatch properties and set exporter `exporter.class=io.dable.jmx.Exporter4CloudwatchCli`

### with [CloudWatch] java SDK

You need to provide jar from [AWS SDK for java](http://aws.amazon.com/java/) (1.2.0) into the classpath.
Configure (in property file) the cloudwatch properties and set exporter `exporter.class=io.dable.jmx.Exporter4CloudwatchAsyncClient`.

# Why

* Want a bulk request (or at least several request per connection)

# References

* [JMX Home](http://download.oracle.com/javase/6/docs/technotes/guides/jmx/index.html)
* [java.lang.instrument](http://download.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html)

    [CloudWatch]: http://aws.amazon.com/fr/cloudwatch/
