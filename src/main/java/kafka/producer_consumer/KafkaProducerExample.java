package kafka.producer_consumer;

import org.apache.kafka.clients.producer.Producer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaProducerExample {
  int producers = 2;
  int sendMessageCount = Constants.MESSAGE_COUNT;
  ExecutorService executorService = Executors.newFixedThreadPool(producers);

  public static void main(String[] args) {
    KafkaProducerExample kafkaProducerExample = new KafkaProducerExample();
    kafkaProducerExample.runProducer();
  }

  public void runProducer() {
    for (int id = 0; id < producers; id++) {
      Producer<String, String> producer = ProducerCreator.createProducer();
      ProducerRunnableTask runner = new ProducerRunnableTask(producer, id, sendMessageCount);
      executorService.submit(runner);
    }
    executorService.shutdown();
  }
}
