# Spark and friends

## Spark

### Installation
Download from: https://spark.apache.org/downloads.html.

unzip spark-2.3.1-bin-hadoop2.7.tgz

move spark-2.3.1-bin-hadoop2.7 under /Library

vim .zshrc export SPARK_HOME=/Library/spark-2.3.1-bin-hadoop2.7

### Spark Standalone
[Documentation](https://spark.apache.org/docs/latest/spark-standalone.html)

#### run master
`./sbin/start-master.sh -h localhost`

Web ui at: http://localhost:8080

Notice spark url, which will be taken as a parameter when running worker. 

Job ui at:http://localhost:4040/jobs

#### run worker(slave)
`./sbin/start-slave.sh <master-spark-URL>`

for example:
`./sbin/start-slave.sh spark://localhost:7077`

After this, in http://localhost:8080, there will be one work in Workers.

#### start a job
See SimpleExample for what does the job do.

Run SimpleExample in Intellij or by ./bin/spark-submit
``````
# Under SparkAndFriends project directory.
$ mvn package
$ /Library/spark-2.3.1-bin-hadoop2.7/bin/spark-submit \
  --class "SimpleExample" \
  --master local \
  target/spark-and-friends-1.0-SNAPSHOT.jar

``````

Tips-1: Before starting a job, you must have a master and at least one worker.
Or else your job can not start and you will see the error of
"WARN TaskSchedulerImpl: Initial job has not accepted any resources; check your cluster UI to ensure that workers are registered and have sufficient resources".

Tips-2: Must set `SparkSession.config("spark.jars", "target/spark-and-friends-1.0-SNAPSHOT.jar")` when using lambda  to make sure intellij can run SimpleExample.
Refer to https://stackoverflow.com/questions/39953245/how-to-fix-java-lang-classcastexception-cannot-assign-instance-of-scala-collect.

Tips-3: Must have a toolchains.xml under ~/.m2, or else you can not build maven package.

The Toolchains Plugins allows to share configuration across plugins.
For example to make sure the plugins like compiler, surefire, javadoc, webstart etc. all use the same JDK for execution.
Similarly to maven-enforcer-plugin, it allows to control environmental constraints in the build.

This toolchains.xml example:
``````
<?xml version="1.0" encoding="UTF8"?>
<toolchains>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>1.8</version>
        </provides>
        <configuration>
            <jdkHome>/your-jdk8-home</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
``````

Tips-4: Must set serialVersionUID, as `private static final long serialVersionUID = 1L;` value to avoid java.io.InvalidClassException :
``````
java.io.InvalidClassException; local class incompatible: stream classdesc serialVersionUID = abc, local class serialVersionUID = -xyz
``````




#### Other Commands

##### close master or other
``````
$ lsof -i :8080 | grep LISTEN
java    22355 ***  286u  IPv6 ****      0t0  TCP *:http-alt (LISTEN)
$ PS -ef 22355
$ kill -9 22355
``````

##### connect an application to the cluster
`./bin/spark-shell --master spark://IP:PORT`

for example:
`./bin/spark-shell --master spark://localhost:7077`

After this, in http://localhost:8080, there will be one application in Running Applications.

After close the application by tabbing "control + C", in http://localhost:8080, there will be one application in Completed Applications.
