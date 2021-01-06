package abcd.spc.sstreams.sg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@Import(abcd.spc.sstreams.sg.RunKeTest.RunKeTestConfiguration.class)
public class RunKeTest extends RunBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test() throws Exception {
        run(mockMvc, objectMapper);
    }

    @TestConfiguration
    static class RunKeTestConfiguration {
        @Autowired
        private RunCache runCache;

        @Autowired
        private RunClient runClient;

        @Bean
        public RunCalculator runCalculator() {
            RunCalculatorWithWork runCalculatorWithWork = new RunCalculatorWithWork();
            runCalculatorWithWork.runCache = runCache;
            runCalculatorWithWork.runClient = runClient;
            return runCalculatorWithWork;
        }
    }
}
