package com.lazaro.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.lazaro.inventory.business.InventoryBusiness;
import com.lazaro.inventory.controller.InventoryController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest({InventoryController.class})
public class TratadoraDeExceptionsTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean 
    private InventoryBusiness inventoryBusiness;

    @Test
    void shouldReturnBadRequestWhenMethodArgumentNotValidExceptionIsThrown() throws Exception {
        mockMvc.perform(post("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "locationId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                            "quantity": -1
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(2))); 
    }
}