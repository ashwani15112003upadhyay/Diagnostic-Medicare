package com.Diagnostic;

import com.Diagnostic.controller.AppointmentController;
import com.Diagnostic.dto.AppointmentCheckupRequest;
import com.Diagnostic.dto.AppointmentCheckupResponse;
import com.Diagnostic.service.AppointmentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

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

    @Test
    void testApply_Success() throws Exception {
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        response.setAppointmentId("abc123");
        response.setPatientName("Alice");

        when(appointmentService.bookAppointment(any())).thenReturn(response);

        String body = "{"
                + "\"patientName\":\"Alice\","
                + "\"age\":25,"
                + "\"gender\":\"Female\","
                + "\"mobile\":\"9876543210\","
                + "\"email\":\"alice@example.com\","
                + "\"checkupType\":\"Blood Test\","
                + "\"preferredDate\":\"" + LocalDate.now().plusDays(2) + "\","
                + "\"preferredTime\":\"09:00:00\""
                + "}";

        mockMvc.perform(post("/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Appointment booked successfully"))
                .andExpect(jsonPath("$.data.patientName").value("Alice"));
    }

    @Test
    void testDeleteAppointment_Success() throws Exception {
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        response.setAppointmentId("test-id");

        when(appointmentService.cancelAppointmentById("test-id")).thenReturn(response);

        mockMvc.perform(delete("/appointment/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Appointment deleted successfully"))
                .andExpect(jsonPath("$.data.appointmentId").value("test-id"));
    }

    @Test
    void testGetAppointmentDetails_Success() throws Exception {
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        response.setAppointmentId("test-id");

        when(appointmentService.getAppointmentById("test-id")).thenReturn(response);

        mockMvc.perform(get("/appointment/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Appointment found successfully"))
                .andExpect(jsonPath("$.data.appointmentId").value("test-id"));
    }

    @Test
    void testGetAllAppointments_Success() throws Exception {
        AppointmentCheckupResponse response1 = new AppointmentCheckupResponse();
        response1.setAppointmentId("id1");

        AppointmentCheckupResponse response2 = new AppointmentCheckupResponse();
        response2.setAppointmentId("id2");

        when(appointmentService.getAllAppointments()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/appointment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].appointmentId").value("id1"))
                .andExpect(jsonPath("$.data[1].appointmentId").value("id2"));
    }

    @Test
    void testUpdateAppointment_Success() throws Exception {
        AppointmentCheckupResponse response = new AppointmentCheckupResponse();
        response.setAppointmentId("test-id");
        response.setPatientName("Alice Updated");

        when(appointmentService.updateAppointment(eq("test-id"), any())).thenReturn(response);

        String body = "{"
                + "\"patientName\":\"Alice Updated\","
                + "\"age\":25,"
                + "\"gender\":\"Female\","
                + "\"mobile\":\"9876543210\","
                + "\"email\":\"alice@example.com\","
                + "\"checkupType\":\"Blood Test\","
                + "\"preferredDate\":\"" + LocalDate.now().plusDays(2) + "\","
                + "\"preferredTime\":\"09:00:00\""
                + "}";

        mockMvc.perform(put("/appointment/test-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Appointment updated successfully"))
                .andExpect(jsonPath("$.data.appointmentId").value("test-id"))
                .andExpect(jsonPath("$.data.patientName").value("Alice Updated"));
    }
}
