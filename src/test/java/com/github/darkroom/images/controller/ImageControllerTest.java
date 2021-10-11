package com.github.darkroom.images.controller;

import com.github.darkroom.images.ImageService;
import com.github.darkroom.images.ImageType;
import com.github.darkroom.images.repository.ImageDetails;
import okhttp3.Headers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ImageControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ImageService imageService;

    @Test
    public void getAll() throws Exception {
        var imageHeaders = new Headers.Builder().add("key", "value").build();
        var imageDetails = new ImageDetails(imageHeaders, new byte[]{});
        Mockito.when(imageService.loadImageForPath("my/path", ImageType.ORIGINAL)).thenReturn(imageDetails);
        var request = get("/images/my/path")
                .queryParam("type", ImageType.ORIGINAL.toString());
        mvc.perform(request).andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("key", "value"))
                .andExpect(content().bytes(imageDetails.data()));
    }
}
