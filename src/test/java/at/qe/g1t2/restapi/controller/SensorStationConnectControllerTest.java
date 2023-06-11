package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.restapi.exception.EntityNotFoundException;
import at.qe.g1t2.restapi.model.SensorStationDTO;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.VisibleSensorStationsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
class SensorStationConnectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    SensorStationService sensorStationService;
    @Autowired
    AccessPointService accessPointService;

    @Autowired
    VisibleSensorStationsService visibleSensorStationsService;
    @Autowired
    SensorStationConnectController sensorStationConnectController;

    @Test
    @DirtiesContext
    @WithMockUser(username = "43d5aba9-29c5-49b4-b4ec-2d430e34104f", authorities = {"ACCESS_POINT"})
    void registerVisibleSensorStation() throws Exception {
        AccessPoint accessPoint = accessPointService.loadAccessPoint("43d5aba9-29c5-49b4-b4ec-2d430e34104f");
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setDipId(23L);
        sensorStationDTO.setMac("1234");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(sensorStationDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorStation/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Assertions.assertNotNull(visibleSensorStationsService.getVisibleMap().get(accessPoint).get(sensorStationDTO.getDipId()));

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void registerExistingSensorStation() throws Exception {
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setDipId(1L);
        sensorStationDTO.setMac("14");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(sensorStationDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorStation/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        Assertions.assertEquals("false", result.getResponse().getContentAsString());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void registerSensorStationWithExistingDipId() throws Exception {
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setDipId(20L);
        sensorStationDTO.setMac("14");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(sensorStationDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorStation/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        Assertions.assertEquals("false", result.getResponse().getContentAsString());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void registerTwoSensorStationWithSameDipId() throws Exception {
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setDipId(1L);
        sensorStationDTO.setMac("100");
        SensorStationDTO sensorStationDTO2 = new SensorStationDTO();
        sensorStationDTO2.setDipId(1L);
        sensorStationDTO2.setMac("200");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(sensorStationDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorStation/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        Assertions.assertEquals("true", result.getResponse().getContentAsString());
        requestBody = objectMapper.writeValueAsString(sensorStationDTO2);
        result = mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorStation/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        Assertions.assertEquals("false", result.getResponse().getContentAsString());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "43d5aba9-29c5-49b4-b4ec-2d430e34104f", authorities = {"ACCESS_POINT"})
    void registerExistingSensorStationOtherAccessPoint() throws Exception {
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setDipId(1L);
        sensorStationDTO.setMac("14");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(sensorStationDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorStation/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        Assertions.assertEquals("false", result.getResponse().getContentAsString());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testGetLimitsAndConnection() throws Exception {
        AccessPoint accessPoint = accessPointService.loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb");

        long x = Long.parseLong("100");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/limits/" + x))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Assertions.assertTrue(accessPointService.loadAccessPoint(accessPoint.getAccessPointID()).getConnected());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/limits/8"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Assertions.assertTrue(accessPointService.loadAccessPoint(accessPoint.getAccessPointID()).getConnected());
        Assertions.assertTrue(sensorStationService.loadSensorStation("fac9c9b9-62cc-4d3d-af5b-06d9965f0f7c").getConnected());

    }


    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testCheckVerifiedSensorstationForCouple() throws Exception {
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setDipId(93L);
        sensorStationDTO.setMac("1234");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(sensorStationDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorStation/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/verified/93"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals("false", result.getResponse().getContentAsString());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testCheckEnabled() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/enabled/4"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    }


    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testCheckEnabledWithNonExistingStation() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/enabled/204"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testConnectExistingSensorStation() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/connected/4"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals("true", result.getResponse().getContentAsString());
        SensorStation station = sensorStationService.loadSensorStation("c57004cc-ca72-41a0-9432-754e642696cb");
        Assertions.assertEquals(true, station.getConnected());

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testConnectVerifiedSensorStation() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/connected/4"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals("true", result.getResponse().getContentAsString());
        SensorStation station = sensorStationService.loadSensorStation("c57004cc-ca72-41a0-9432-754e642696cb");
        Assertions.assertEquals(true, station.getConnected());

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testConnectUnverifiedSensorStation() throws Exception {
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setMac("345456");
        sensorStationDTO.setDipId(100L);
        visibleSensorStationsService.addVisibleStation(accessPointService.loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb"), sensorStationDTO);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/connected/101"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());


    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testConnectUnregisteredSensorStation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/connected/100"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testConnectNewSensorStation() throws Exception {
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setMac("345456");
        sensorStationDTO.setDipId(100L);
        visibleSensorStationsService.addVisibleStation(accessPointService.loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb"), sensorStationDTO);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/connected/100"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testRefreshConnectionSensorStation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/refresh/3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        SensorStation sensorStation = sensorStationService.loadSensorStation("9f98b70c-4de7-46c0-a611-21160743be7e");
        Assertions.assertEquals(true, sensorStation.getConnected());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4494ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testUnregisteredAccessPoint() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> sensorStationConnectController.getAuthAccessPoint());
    }


    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testGardnerIsHere() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/gardenerHere/3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testTimeout() throws Exception {
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setMac("345456");
        sensorStationDTO.setDipId(100L);
        visibleSensorStationsService.addVisibleStation(accessPointService.loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb"), sensorStationDTO);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/timeout/100"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        sensorStationDTO = visibleSensorStationsService.getSensorStationByAccessPointAndDipId(accessPointService.loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb"), "100");
        Assertions.assertTrue(sensorStationDTO.getConnectingTimeOut());
        Assertions.assertEquals(false, sensorStationDTO.getVerified());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testTimeoutEmptyMap() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/timeout/120"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4494ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testSatisfyConnectionNoAccessPoint() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> sensorStationConnectController.satisFyConnection(null, "100"));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "4494ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testUnregisteredAccessPoint2() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> sensorStationConnectController.satisFyConnection(null));
    }


}