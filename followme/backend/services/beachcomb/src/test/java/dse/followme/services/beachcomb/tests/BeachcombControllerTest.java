package dse.followme.services.beachcomb.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import dse.followme.services.beachcomb.exceptions.VehicleNotFoundException;
import dse.followme.services.beachcomb.model.dtos.MotionDataDTO;
import dse.followme.services.beachcomb.model.enums.Role;
import dse.followme.services.beachcomb.service.MotionDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class BeachcombControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MotionDataService motionDataService;

    private final MotionDataDTO testMotionDataDTO1 = new MotionDataDTO(
            TestUtils.TEST_DATA_1_VIN,
            TestUtils.TEST_DATA_1_LAT,
            TestUtils.TEST_DATA_1_LONG,
            Role.FOLLOWING_VEHICLE,
            TestUtils.TEST_DATA_1_LANE,
            TestUtils.TEST_DATA_1_SPEED,
            TestUtils.date()
    );

    @Test
    public void setMotionData_whenDataValid_returnsOk() throws Exception {
        when(motionDataService.setMotionData(any(MotionDataDTO.class)))
                .thenReturn(testMotionDataDTO1);

        mockMvc.perform(MockMvcRequestBuilders.post("/beachcomb")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMotionDataDTO1)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vin").value(TestUtils.TEST_DATA_1_VIN))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lon").value(TestUtils.TEST_DATA_1_LONG))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lat").value(TestUtils.TEST_DATA_1_LAT));
    }

    @Test
    public void getMotionData_whenVehicleNotInDb_returnsHttpErrorNotFound() throws Exception {
        when(motionDataService.getMotionData(any(String.class)))
                .thenThrow(VehicleNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/beachcomb/{}", TestUtils.TEST_DATA_1_VIN))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
