package kafka.producer_consumer;

public interface Constants {
  String KAFKA_BROKERS = "localhost:9092,localhost:9093,localhost:9094";

  int MESSAGE_COUNT = 100;

  long POLL_TIME_OUT_MILLI_SECOND = 1000;

  String CLIENT_ID = "client";

  String TOPIC_NAME = "test";

  String GROUP_ID_CONFIG = "consumerGroup";

  String OFFSET_RESET_LATEST = "latest";

  String OFFSET_RESET_EARLIER = "earliest";

  int MAX_POLL_RECORDS = 1;
}
