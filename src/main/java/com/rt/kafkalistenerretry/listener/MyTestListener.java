package com.rt.kafkalistenerretry.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class MyTestListener {
    @RetryableTopic(
            attempts = "${kafka-retry-attempts}",
            backoff = @Backoff(delayExpression = "${kafka-retry-delay}",
                    multiplierExpression = "${kafka-retry-multiplier}",
                    maxDelayExpression = "${kafka-retry-max-delay}"
            ),
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "${test-event-topic-name}")
    public void consume(
            String message,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String realTopic) {

        System.out.println("message -> " + message);
        System.out.println("Topic -> " + realTopic);
            if (message.contains("a")) {
                throw new RuntimeException();
            }


        acknowledgment.acknowledge();
        // https://www.confluent.io/blog/configuring-apache-kafka-consumer-group-ids/#:~:text=The%20Group%20ID%20determines%20which,reading%20from%20the%20same%20topic.
    }
}
