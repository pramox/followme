package dse.followme.services.control;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dse.followme.services.control.dtos.FollowerStatusDTO;
import dse.followme.services.control.dtos.LeaderStatusDTO;
import dse.followme.services.control.dtos.MatchDTO;
import dse.followme.services.control.dtos.MotionDataDTO;
import dse.followme.services.control.entities.Match;
import dse.followme.services.control.repositories.MatchesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MatchesRepository matchesRepository;

    @Test
    void validateFollowerPosition_ShouldReturnFollowerStatusDTO() throws Exception {
        MotionDataDTO motionDataDTO = new MotionDataDTO("follower123", 1, 60.0, LocalDateTime.now());

        var res = mockMvc.perform(MockMvcRequestBuilders.post("/vehicle/following")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(motionDataDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = res.getResponse().getContentAsString();
        var followerStatus = objectMapper.readValue(responseBody, FollowerStatusDTO.class);

        assertEquals(followerStatus.getIsFollowMeMode(), false);
        assertNull(followerStatus.getActualLane());
    }

    @Test
    void validateFollowerPosition_withMatchInserted_ShouldReturnFollowerStatusDTO() throws Exception {
        MotionDataDTO motionDataDTO = new MotionDataDTO("follower", 1, 60.0, LocalDateTime.now());
        Match match = new Match("follower", "leader", LocalDateTime.now(), 20., 2, 20., null);
        matchesRepository.deleteAll();
        matchesRepository.insert(match);

        var res = mockMvc.perform(MockMvcRequestBuilders.post("/vehicle/following")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(motionDataDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = res.getResponse().getContentAsString();
        System.out.println(responseBody);
        var followerStatus = objectMapper.readValue(responseBody, FollowerStatusDTO.class);

        assertEquals(followerStatus.getIsFollowMeMode(), true);
        assertEquals(followerStatus.getRequiredSpeed(), 20);
        assertEquals(followerStatus.getLeaderVin(), "leader");
        assertNull(followerStatus.getActualLane());
    }

    @Test
    void validateLeaderPosition_ShouldReturnLeaderStatusDTO() throws Exception {
        MotionDataDTO motionDataDTO = new MotionDataDTO("leader123", 2, 80.0, LocalDateTime.now());

        var res = mockMvc.perform(MockMvcRequestBuilders.post("/vehicle/leading")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(motionDataDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = res.getResponse().getContentAsString();
        var leaderStatus = objectMapper.readValue(responseBody, LeaderStatusDTO.class);

        assertEquals(leaderStatus.getIsFollowMeMode(), false);
        assertNull(leaderStatus.getFollowerVin());
    }

    @Test
    void findAllMatches_ShouldReturnListOfEmptyMatchDTOs() throws Exception {
        matchesRepository.deleteAll();
        var res = mockMvc.perform(MockMvcRequestBuilders.get("/vehicle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = res.getResponse().getContentAsString();
        var matches = objectMapper.readValue(responseBody, List.class);
        assertNotNull(matches);
        assertEquals(matches.size(), 0);
    }

    @Test
    void findAllMatches_ShouldReturnListOfMatchDTOs() throws Exception {
        Match match = new Match("follower", "leader", LocalDateTime.now(), 20., 2, 20., null);
        matchesRepository.deleteAll();
        matchesRepository.insert(match);
        var res = mockMvc.perform(MockMvcRequestBuilders.get("/vehicle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = res.getResponse().getContentAsString();
        List<MatchDTO> matches = objectMapper.readValue(responseBody, new TypeReference<>(){});
        assertNotNull(matches);
        assertEquals(matches.get(0).getLeaderVin(), "leader");
        assertEquals(matches.size(), 1);
    }
}