package abcd.spc.l.streams.sr;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

public class ActiveTasksAppTest {
    @Test
    void test() throws InterruptedException {
        String brokerHost = "127.0.0.1";
        int brokerPort = 29092;
        String activeTasksTopic = "active_tasks";
        try (KafkaServerService kafkaServerService = new KafkaServerService(brokerHost, brokerPort)) {
            kafkaServerService.start();
            kafkaServerService.createTopic(activeTasksTopic);
            ActiveTasksApp.ActiveTasksService activeTasksService =
                    new ActiveTasksApp.ActiveTasksService(
                            brokerHost + ":" + brokerPort, activeTasksTopic);
            activeTasksService.run();
            KafkaProducer<String, String> activeTasksProducer = kafkaServerService.createKafkaProducerStringString();
            ProducerRecord<String, String> data =
                    new ProducerRecord<>(activeTasksTopic, "42", "message");
            activeTasksProducer.send(data);
            activeTasksProducer.flush();
            activeTasksProducer.close();
            Thread.sleep(6000);
            activeTasksService.close();
        }
    }
}
