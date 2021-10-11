package com.github.darkroom.collections.controller;

import com.github.darkroom.collections.CollectionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CollectionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CollectionService collectionService;

    @Test
    public void getAll() throws Exception {
        var responseMock = List.of(new Collection(42L, "text"));
        Mockito.when(collectionService.loadAll()).thenReturn(responseMock);
        var request = get("/collections");
        mvc.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""
                        [{
                            "id": 42,
                            "name": "text"
                        }]
                        """));
    }
}
