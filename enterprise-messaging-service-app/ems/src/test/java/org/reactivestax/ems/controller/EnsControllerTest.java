package org.reactivestax.ems.controller;

import org.junit.jupiter.api.Test;
import org.reactivestax.ems.TestDataProvider;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.service.EnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnsController.class)
public class EnsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnsService ensService;

    @Test
    void testSendMessageWithSmsGoodCustomer() throws  Exception {
        String customerJson = TestDataProvider.goodCustomerJson.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                .content(customerJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Message Sent Via SMS."));
    }

    @Test
    void testSendMessageWithSmsGoodCustomerWithPhoneNumber() throws  Exception {
        String customerJson = TestDataProvider.goodCustomerJsonWithPhoneNumber.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Message Sent Via SMS."));
    }

    @Test
    void testSendMessageWithSmsGoodCustomerWithEmail() throws  Exception {
        String customerJson = TestDataProvider.goodCustomerJsonWithEmail.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Message Sent Via SMS."));
    }

    @Test
    void testSendMessageWithSmsGoodCustomerWithPhNumAndEmail() throws  Exception {
        String customerJson = TestDataProvider.goodCustomerJsonWithPhoneNumAndEmail.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Message Sent Via SMS."));
    }

}
