package dse.followme.services.beachcomb.tests;

import dse.followme.services.beachcomb.model.dtos.MotionDataDTO;
import dse.followme.services.beachcomb.repository.MotionDataRepository;
import dse.followme.services.beachcomb.service.MotionDataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class MotionDataServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MotionDataRepository motionDataRepository;

    @Autowired
    private MotionDataService vehiclePositionService;

    @Test
    public void setMotionData() {
        MotionDataDTO response = vehiclePositionService.setMotionData(TestUtils.testMotionDataDTO1);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getTimestamp());
        Assertions.assertEquals(TestUtils.TEST_DATA_1_VIN, response.getVin());
    }

}
