package kafka.producer_consumer;

import org.apache.kafka.clients.consumer.Consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class KafkaConsumerExample {
  int workers = 4;
  ExecutorService executorService = Executors.newFixedThreadPool(workers + 1);
  List<ConsumerRunnableTask> runners = new ArrayList<>();

  public static void main(String[] args) {
    KafkaConsumerExample kafkaConsumerExample = new KafkaConsumerExample();
    kafkaConsumerExample.runConsumer();

    // or manually shutdown by stop the main thread, stop button or control C
    kafkaConsumerExample.shutdownHoot();

    // shutdown in 10 minutes
    kafkaConsumerExample.shutDown(10);
  }

  public void runConsumer() {
    for (int id = 0; id < workers; id++) {
      Consumer<String, String> consumer = ConsumerCreator.createConsumer(id);
      System.out.println("Submitting consumer ID " + id);
      ConsumerRunnableTask runner = new ConsumerRunnableTask(consumer, id);
      executorService.submit(runner);
      runners.add(runner);
    }

    try {
      sleep(40000);
      runners.get(0).shutdown();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void shutDown(long delayInMinutes) {
    TimerTask timerTask =
        new TimerTask() {
          @Override
          public void run() {
            for (ConsumerRunnableTask runner : runners) {
              System.out.println("Shutting down consumer ID " + runner.getId());
              runner.shutdown();
            }
            executorService.shutdown();
            try {
              executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        };
    Timer timer = new Timer(false);
    timer.schedule(timerTask, delayInMinutes * 60 * 1000);
  }

  public void shutdownHoot() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  for (ConsumerRunnableTask consumer : runners) {
                    System.out.println(
                        "Shutting down consumer "
                            + consumer.getId()
                            + ", total message received "
                            + consumer.getReceivedMessageCount());
                    consumer.shutdown();
                  }
                  executorService.shutdown();
                  try {
                    executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }));
  }
}
