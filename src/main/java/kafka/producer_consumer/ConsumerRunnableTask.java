package kafka.producer_consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerRunnableTask implements Runnable {
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final Consumer<String, String> consumer;
  private final int id;
  private int receivedMessageCount = 0;

  public ConsumerRunnableTask(Consumer<String, String> consumer, int id) {
    this.consumer = consumer;
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public int getReceivedMessageCount() {
    return receivedMessageCount;
  }

  @Override
  public void run() {
    try {
      while (!closed.get()) {
        ConsumerRecords<String, String> records =
            consumer.poll(Duration.ofMillis(Constants.POLL_TIME_OUT_MILLI_SECOND));
        for (TopicPartition partition : records.partitions()) {
          List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
          for (ConsumerRecord<String, String> record : partitionRecords) {
            receivedMessageCount++;
            System.out.printf(
                "Consumer ID = %d, offset = %d, key = %s, value = %s%n",
                id, record.offset(), record.key(), record.value());
          }
          long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
          consumer.commitSync(
              Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
        }
      }
      System.out.println("Total received messages " + receivedMessageCount);
    } catch (WakeupException e) {
      // Ignore exception if closing
      if (!closed.get()) throw e;
    } finally {
      consumer.close();
    }
  }

  // Shutdown hook which can be called from a separate thread
  public void shutdown() {
    closed.set(true);
    consumer.wakeup();
  }
}
