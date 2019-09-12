package neil.demo.fitba;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationRunner implements CommandLineRunner {

    private static CountDownLatch countDownLatch;
    private static AtomicLong onSuccessCount;
    private static AtomicLong onFailureCount;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

       @Override
       public void run(String... args) throws Exception {

               System.out.println("===================");
               System.out.println("- START -");
               System.out.println("===================");
               System.out.println("");

               var first = Arrays.asList(JsonData.ACCOUNT_BASELINE);
               this.output(first);
               
               System.out.println("");
               System.out.println("===================");
               System.out.println("");
               System.out.print("Press ENTER a few times when ready for second: ");
               System.in.read();
               System.in.read();
               System.in.read();
               System.in.read();
               
               System.out.println("");
               System.out.println("===================");
               System.out.println("");
               
               var second = Arrays.asList(JsonData.ACCOUNT_TRANSACTIONS);
               this.output(second);
               
               System.out.println("");
               System.out.println("===================");
               System.out.println("");
               System.out.print("Press ENTER a few times when ready for third: ");
               System.in.read();
               System.in.read();
               System.in.read();
               System.in.read();
               
               System.out.println("");
               System.out.println("===================");
               System.out.println("");
               
               var third = Arrays.asList(JsonData.MORE_ACCOUNT_TRANSACTIONS);
               this.output(third);

               System.out.println("");
               System.out.println("===================");
               System.out.println("- END -");
               System.out.println("===================");
       }
       
       
       private void output(List<String> lines) throws Exception {
               countDownLatch = new CountDownLatch(lines.size());
        onFailureCount = new AtomicLong(0);
        onSuccessCount = new AtomicLong(0);
        
               for (String json : lines) {
            TimeUnit.SECONDS.sleep(5);
            this.write(json);
               }
               
               countDownLatch.await();

               if (onFailureCount.get() > 0) {
                       throw new RuntimeException(onFailureCount.get() + " failures");
               } else {
                       if (onSuccessCount.get() != lines.size()) {
                               throw new RuntimeException(onSuccessCount.get() + " successes != expected " + lines.size());
                       } else {
                               log.info("Wrote {} record{} to topic '{}'", 
                               onSuccessCount.get(), (onSuccessCount.get() == 1 ? "" : "s"),
                               this.kafkaTemplate.getDefaultTopic());
                       }       
               }
       }

       private void write(String value) throws Exception {
               JSONObject jsonObject = new JSONObject(value);
               
               String key = jsonObject.getString("account");
               int partition = key.hashCode() % 3;
               
               log.info("Account: '{}' partition {} JSON => {}", key, partition, jsonObject.toString(4));

               ListenableFuture<SendResult<String, String>> sendResult =
                kafkaTemplate.sendDefault(partition, key, value);

               sendResult.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
               @Override
               public void onSuccess(SendResult<String, String> sendResult) {
                       onSuccessCount.incrementAndGet();
                       ProducerRecord<String, String> producerRecord = sendResult.getProducerRecord();
                       countDownLatch.countDown();
                       log.trace("wrote '{}'=='{}'", producerRecord.key(), producerRecord.value());
               }

               @Override
               public void onFailure(Throwable t) {
                       onFailureCount.incrementAndGet();
                       countDownLatch.countDown();
                       log.error("onFailure()", t);
               }
               });     
       }
       
}
