package com.example.kafkamcpserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class KafkaConsumerService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Tool(name = "getKafkaData", description = "fetch kafka data by topic-name")
    public List<JsonNode> getLiveKafkaData(String topicName) {
        Properties props = new Properties();
        KafkaConsumer<String, String> consumer = null;
        List<JsonNode> jsonNodes = new ArrayList<>();

        try {
            props.put("bootstrap.servers", bootstrapServers);
            props.put("group.id", UUID.randomUUID().toString());
            props.put("key.deserializer", StringDeserializer.class.getName());
            props.put("value.deserializer", StringDeserializer.class.getName());
            props.put("auto.offset.reset", "earliest");

            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Collections.singletonList(topicName));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(15000));
            int i=0;
            for (ConsumerRecord<String, String> record : records) {
                String jsonString = record.value();
                try {
                    if(i<10){
                        JsonNode jsonNode = objectMapper.readTree(jsonString);
                        jsonNodes.add(jsonNode);
                        i++;
                    }
                    else {
                        break;
                    }
                } catch (JsonProcessingException e) {
                    System.err.println("Error parsing JSON from Kafka record (topic: " + record.topic() + ", partition: " + record.partition() + ", offset: " + record.offset() + "): " + jsonString + " - " + e.getMessage());
                }
            }
            System.out.println(jsonNodes);
            return jsonNodes;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Collections.emptyList(); // Return an empty list in case of an exception
        } finally {
            if (consumer != null) {
                consumer.close();
                System.out.println("Kafka consumer closed.");
            }
        }
    }
}