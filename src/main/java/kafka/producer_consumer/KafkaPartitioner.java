package kafka.producer_consumer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class KafkaPartitioner implements Partitioner {

  static int partitioner = 0;
  String topic;

  public KafkaPartitioner() {}

  public KafkaPartitioner(String topic) {
    this.topic = topic;
  }

  @Override
  public int partition(
      String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
    int count = cluster.partitionCountForTopic(Constants.TOPIC_NAME);
    return partitioner++ % count;
  }

  @Override
  public void close() {}

  @Override
  public void configure(Map<String, ?> map) {}
}
