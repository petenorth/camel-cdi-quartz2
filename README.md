Camel Quartz2 Example
----------------------

This example demonstrates using the camel-quartz2 component with WildFly Camel susbsystem to produce and consume JMS messages.

Prerequisites
-------------

* Maven
* An application server with the wildfly-camel subsystem installed

Running the example
-------------------

Firstly a mysql instances needs to be running and a schema created for Quartz2.

If docker is installed then simple execute
 
    docker run -p 3306:3306 --name some-mysql -e MYSQL_ROOT_PASSWORD=admin -e MYSQL_DATABASE=quartz -d mysql:latest
    
To kill the container and remove the docker image

    docker kill some-mysql
    docker rm some-mysql
    
Note that the username for the database is root and the password is admin.

Then to create the schema run the script

    src/quartz2_setup.sql
    
within the quartz database.

To understand how clustering in Quartz works read

http://www.quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigJDBCJobStoreClustering.html

Essentially a Quartz Scheduler comprises of multiple scheduler runtimes connecting to a single database (which must be made resilient if it is not to be a single point of failure). 

In the case of Fuse on EAP the scheduler runtime is running within an EAP application server. 

The quartz component is configured via an external file the location of which is supplied as an java system property `quartz.properties`.

`/home/pfry/projects/myproject/quartz.properties`

Sample contents for this file is located in 

`src/main/resources/quartz.properties`

The camel quartz module needs to be able to access the mysql drivers. Therefore the following changes are required:

1) in `$EAP_HOME/modules/system/layers/fuse/org/quartz/main/module.xml` add a line to access the driver library (in the following the postgres entry is commented out):

    <?xml version="1.0" encoding="UTF-8"?>
    <module xmlns="urn:jboss:module:1.1" name="org.quartz">
        <properties>
          <property name="jboss.api" value="private" />
        </properties>
        <resources>
          <resource-root path="c3p0-0.9.1.2.jar" />
          <resource-root path="quartz-2.2.1.jar" />
          <resource-root path="mysql-connector-java-5.1.39.jar" />
          <!--<resource-root path="postgresql-9.4.1209.jre7.jar" />-->
        </resources>
        <dependencies>
          <module name="javax.api" />
          <module name="org.slf4j" />
        </dependencies>
        <exports>
          <exclude path="com/mchange**" />
          <exclude path="org/terracotta**" />
        </exports>
    </module> 
    
2) copy mysql-connector-java-5.1.39.jar (or postgres driver library if required) to  `$EAP_HOME/modules/system/layers/fuse/org/quartz/main`

To run the example:

1. Start the application server in standalone mode `${JBOSS_HOME}/bin/standalone.sh -Dquartz.properties=\the\location\of\the\quartz.properties`
2. Build and deploy the project `mvn install -Pdeploy`


Description of the route
------------------------

The route itself is located in

     /quartz2-demo/src/main/resources/OSGI-INF/blueprint/blueprint.xml
     
it schedules a job named demoQuartzCluster to run every 2 seconds. It is configured to be stateful which means that it 'Uses a Quartz @PersistJobDataAfterExecution and @DisallowConcurrentExecution instead of the default job.' 

To test this a delay is present in the route of 10 secs meaning that the job will take longer than the scheduled frequency. The presence of the stateful=true property means that concurrency is not allowed and that it will wait until the job is completed (after 10 secs) before firing another job).

The different schedulers within the cluster compete for jobs but the Quartz documentation states that 'The load balancing mechanism is near-random for busy schedulers (lots of triggers) but favors the same node for non-busy (e.g. few triggers) schedulers.'
    
Configuration of the QuartzComponent
--------------------------------------

Configuration of the QuartzComponent is via a CDI producer method on `org.wildfly.camel.examples.quartz2.Factory.java`. When a route is created via EAP Camel Subsystem it is started via CDI as an application scoped bean. The schemes (URIs) used in the route are collected and the corresponding components are looked up from the registry. The registry in this case is CDI. Therefore by providing a producer method for the Quartz2 we can override the configuration.

Undeploy
--------

To undeploy the example run `mvn clean -Pdeploy`.


