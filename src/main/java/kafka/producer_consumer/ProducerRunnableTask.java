package kafka.producer_consumer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerRunnableTask implements Runnable {
  private final int id;
  private final int sendMessageCount;
  private final Producer<String, String> producer;

  public ProducerRunnableTask(Producer<String, String> producer, int id, int sendMessageCount) {
    this.producer = producer;
    this.id = id;
    this.sendMessageCount = sendMessageCount;
  }

  @Override
  public void run() {
    long time = System.currentTimeMillis();
    try {
      for (long index = time; index < time + sendMessageCount; index++) {
        final ProducerRecord<String, String> record =
            new ProducerRecord<>(
                Constants.TOPIC_NAME,
                id + "-" + index,
                "Hello " + index + " from Producer " + id + ", message " + (index - time));
        RecordMetadata metadata = producer.send(record).get();
        long elapsedTime = System.currentTimeMillis() - time;
        System.out.printf(
            "sent record(key=%s value=%s) " + "meta(partition=%d, offset=%d) time=%d\n",
            record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      producer.flush();
      producer.close();
    }
    System.out.println("Producer " + id + " exit 0");
  }
}
