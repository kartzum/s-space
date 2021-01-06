package abcd.spc.sstreams.sg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RunListener {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RunCalculator runCalculator;

    @KafkaListener(topics = "runs", groupId = "m-group")
    public void receive(ConsumerRecord<?, ?> consumerRecord) {
        String key = consumerRecord.key().toString();
        Run run = null;
        try {
            run = objectMapper.readValue(consumerRecord.value().toString(), Run.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        runCalculator.run(key, run);
    }
}
