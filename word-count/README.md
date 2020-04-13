Word Count - Using Kafka Streams
================================

Follow these steps to run word count kafka streams application. 

Download kafka from [here](https://kafka.apache.org/downloads). Extract to a folder and from terminal change directory to point it to kafka home.
```bash
kafka_2.12-2.2.0$
```

#### #1 Start zookeeper - Tab1
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

#### #2 Start kafka - Tab2
```bash
bin/kafka-server-start.sh config/server.properties
```

#### #3 Create kafka topics
```bash
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-input
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-output
```
#### #4 List topics to check
```bash
bin/kafka-topics.sh --zookeeper localhost:2181 --list

word-count-input
word-count-output
```

#### #5 Start kafka consumer 
Open another tab in terminal and start a consumer, consumer will keep listening on topic `word-count-output`
```bash
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
        --topic word-count-output \
        --from-beginning \
        --formatter kafka.tools.DefaultMessageFormatter \
        --property print.key=true \
        --property print.value=true \
        --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
        --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

```

#### #6 Run Words-count application from IDE or from terminal.

#### #7 Start kafka producer 
Open another tab in terminal and start a producer. Once its started, start typing the words and press enter. Word count java app reads the data from `word-count-input` topic, processes it and enters into `word-count-output` topic.

```bash
bin/kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic word-count-input
>
Kafka streams example
kafka broker example
```

#### #8 Check generated output in consumer tab in previous step
```bash
>
Kafka 2 
streams 1
example 2
broker  1
```
