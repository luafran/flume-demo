Download and extract Apache flume (this demo used Apache Flume 1.6.0)

    ~/work$ wget http://www.apache.org/dyn/closer.lua/flume/1.6.0/apache-flume-1.6.0-bin.tar.gz && tar xvzf apache-flume-1.6.0-bin.tar.gz

Copy flume-config/* to apache-flume-1.6.0-bin/conf directory

    flume$ cp flume-config/* ~/work/apache-flume-1.6.0-bin/conf/

Edit flume-env.sh and change value of FLUME_CLASSPATH to point to the jar where flume custom classes are located

    ~/work/apache-flume-1.6.0-bin$ vim conf/flume-env.sh

Launch ZooKeeper and Kafka on localhost

    ~/work/kafka_2.10-0.9.0.0$ nohup bin/zookeeper-server-start.sh /config/zookeeper.properties &
    ~/work/kafka_2.10-0.9.0.0$ nohup bin/kafka-server-start.sh config/server.properties &

Create kafka topic

    ~/work/kafka_2.10-0.9.0.0$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

Run Kafka consumer

    ~/work/kafka_2.10-0.9.0.0$ bin/kafka-console-consumer.sh --new-consumer --bootstrap-server kafka-1:9092 --topic test

Run a flume agent

    ~/work/apache-flume-1.6.0-bin$ bin/flume-ng agent --conf ./conf/ -f ./conf/poll-to-kafka.conf --name a1 -Dflume.root.logger=INFO,console


