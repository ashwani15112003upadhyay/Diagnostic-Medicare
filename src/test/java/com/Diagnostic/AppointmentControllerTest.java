package com.Diagnostic;


import com.Diagnostic.controller.AppointmentController;
import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AppointmentCheckupRequest getValidRequest() {
        AppointmentCheckupRequest request = new AppointmentCheckupRequest();
        request.setPatientName("Alice");
        request.setAge(25);
        request.setGender("Female");
        request.setMobile("9876543210");
        request.setEmail("alice@example.com");
        request.setCheckupType("Blood Test");
        request.setPreferredDate(LocalDate.now().plusDays(2));
        request.setPreferredTime(LocalTime.of(9, 0));
        return request;
    }

//    @Test
//    void testApply_Success() throws Exception {
//        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
//        when(appointmentService.applyForCheckup(any())).thenReturn(response);
//
//        mockMvc.perform(post("/appointment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(getValidRequest())))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true));
//    }

    @Test
    void testDeleteAppointment_Success() throws Exception {
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        when(appointmentService.deleteAppointmentById("test-id")).thenReturn(response);

        mockMvc.perform(delete("/appointment/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetAppointmentDetails_Success() throws Exception {
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        when(appointmentService.getAppointmentDetailsById("test-id")).thenReturn(response);

        mockMvc.perform(get("/appointment/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

//    @Test
//    void testUpdateAppointment_Success() throws Exception {
//        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
//        when(appointmentService.updateAppointment(eq("test-id"), any())).thenReturn(response);
//
//        mockMvc.perform(put("/appointment/test-id")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(getValidRequest())))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//    }
}

