package com.github.darkroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darkroom.cases.CaseMetadata;
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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
}
