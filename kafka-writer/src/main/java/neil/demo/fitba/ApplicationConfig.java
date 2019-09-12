package neil.demo.fitba;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class ApplicationConfig {

       // Needs to match the script in src/main/scripts
    static final String KAFKA_BOOTSTRAP_SERVERS = "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094";
    static final String KAFKA_TOPIC_NAME = "account";
    static final int KAFKA_PARTITION_COUNT = 3;
       
       @Bean
       public KafkaTemplate<String, String> kafkaTemplate() {
               Map<String, Object> producerConfigs = new HashMap<>();

               producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
               producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
               producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

               ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerConfigs);

               KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);

               kafkaTemplate.setDefaultTopic(KAFKA_TOPIC_NAME);

               return kafkaTemplate;
       }
}
