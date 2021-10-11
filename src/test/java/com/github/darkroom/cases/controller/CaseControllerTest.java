package com.github.darkroom.cases.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darkroom.cases.CaseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class CaseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CaseService caseService;

    @Test
    void sendTest() throws Exception {
        // Meta
        var meta = new CaseMetadata("home", Map.of("key", "value"));
        var metaBytes = objectMapper.writeValueAsBytes(meta);
        var metaPart = new MockMultipartFile("meta", null, MediaType.APPLICATION_JSON_VALUE, metaBytes);
        // Image
        var imagePart = new MockMultipartFile("image", null, MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes());
        var request = multipart("/one/cases")
                .file(metaPart)
                .file(imagePart);
        // Check
        mvc.perform(request).andDo(print()).andExpect(status().isCreated());
        Mockito.verify(caseService).saveCase("one", meta, imagePart);
    }

    @Test
    public void showSeveralCasesSeries() throws Exception {
        Mockito.when(caseService.loadSeries("one", "class")).thenReturn(List.of());
        var request = get("/one/cases")
                .queryParam("classifier", "class");
        mvc.perform(request).andDo(print()).andExpect(status().isOk()).andExpect(content().json("[]"));
        Mockito.verify(caseService, never()).loadRecent(anyString());
    }

    @Test
    public void showSeveralCasesOverview() throws Exception {
        Mockito.when(caseService.loadRecent("collection")).thenReturn(List.of());
        var request = get("/collection/cases");
        mvc.perform(request).andDo(print()).andExpect(status().isOk()).andExpect(content().json("[]"));
        Mockito.verify(caseService, never()).loadSeries("collection", anyString());
    }
}
