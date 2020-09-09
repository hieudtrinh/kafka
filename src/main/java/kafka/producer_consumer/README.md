To install kafka server for mac OS

Installing Java
Prior to installing either Zookeeper or Kafka, you will need a Java 
environment set up and functioning. This should be a Java 8 version, and can 
be the version provided by your OS or one directly downloaded from java.com. 
Though Zookeeper and Kafka will work with a runtime edition of Java, it may 
be more convenient when developing tools and applications to have the full 
Java Development Kit (JDK). The installation


$ brew cask install java
$ brew install kafka

During server start, you might be facing connection broken issue.
[2018-08-28 16:24:41,166] WARN [Controller id=0, targetBrokerId=0] Connection to node 0 could not be established. Broker may not be available. (org.apache.kafka.clients.NetworkClient)
[2018-08-28 16:24:41,268] WARN [Controller id=0, targetBrokerId=0] Connection to node 0 could not be established. Broker may not be available. (org.apache.kafka.clients.NetworkClient)
To fix this issue, we need to change the server.properties file.
$ vim /usr/local/etc/kafka/server.properties
Here uncomment the server settings and update the value from
listeners=PLAINTEXT://:9092
to
############################# Socket Server Settings #############################
# The address the socket server listens on. It will get the value returned from 
# java.net.InetAddress.getCanonicalHostName() if not configured.
#   FORMAT:
#     listeners = listener_name://host_name:port
#   EXAMPLE:
#     listeners = PLAINTEXT://your.host.name:9092
listeners=PLAINTEXT://localhost:9092
and restart the server and it will work great.
Create Kafka Topic:
A topic is a category or feed name to which records are published. Topics in Kafka are always multi-subscriber; that is, a topic can have zero, one, or many consumers that subscribe to the data written to it.
$ kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
Here we have created a topic name test


Initialize Producer console:
Now we will initialize the Kafka producer console, which will listen to localhost at port 9092 at topic test :
$ kafka-console-producer --broker-list localhost:9092 --topic test
>send first message
>send second message
>wow it is working
Initialize Consumer console:
Now we will initialize the Kafka consumer console, which will listen to bootstrap server localhost at port 9092 at topic test from beginning:
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning
send first message
send second message
wow it is working


steps will assume you have installed JDK version 8 update 51 in /usr/java/ jdk1.8.0_51.


To start the kafka server
kafka-server-start /usr/local/etc/kafka/server.properties
To start zookeeper
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties

To start producer 
kafka-console-producer --broker-list localhost:9092 --topic test

To start consumer
kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning
or 
kafka-console-consumer --bootstrap-server localhost:9092 --topic test

To list consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --list


Modify the server.properties
vi /usr/local/etc/kafka/server.properties


# The default number of log partitions per topic. More partitions allow greater
# parallelism for consumption, but this will also result in more files across
# the brokers.
# num.partitions=1
num.partitions=4

# after change the num.partitions=1 to num.partitions=4 for topic "test", it
# does not work. It turned out that I need to remove "test" topic from zookeeper.
kafka-topics --delete --zookeeper localhost:2181  --topic test

# For some reason, the deletion only mark delete but not remove files zookeeper.
# After I remove zookeeper folder
rm -rf /usr/local/var/lib/zookeeper/*
# restart zookeeper and kafka server, it worked find.

# Messages from the "test" topic will be distributed to all consumers.
# i.e. each message only process once by one of the consumers.

