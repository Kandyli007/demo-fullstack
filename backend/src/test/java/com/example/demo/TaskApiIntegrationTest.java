package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void supportsFullTaskLifecycle() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"编写集成测试","description":"验证 CRUD 流程","status":"TODO"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("编写集成测试"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode created = objectMapper.readTree(response);
        long id = created.get("id").asLong();

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TODO"));

        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"编写集成测试","description":"验证 CRUD 流程","status":"DONE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("找不到 ID 为 " + id + " 的任务"));
    }

    @Test
    void rejectsBlankTitle() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"  ","description":"无效请求"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("标题不能为空"));
    }
}
