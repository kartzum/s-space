package abcd.spc.sstreams.sg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class RunBase {
    void run(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        MvcResult keyResult = mockMvc.perform(MockMvcRequestBuilders.post("/runs")
                .content("body")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String key = keyResult.getResponse().getContentAsString();
        RunStatus runStatus = RunStatus.UNKNOWN;
        while (runStatus != RunStatus.DONE) {
            MvcResult statusResult = mockMvc.perform(MockMvcRequestBuilders.get("/runs/" + key + "/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            runStatus = objectMapper.readValue(statusResult.getResponse().getContentAsString(), RunStatus.class);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/runs/" + key)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals("body_calculated", response);
    }
}
