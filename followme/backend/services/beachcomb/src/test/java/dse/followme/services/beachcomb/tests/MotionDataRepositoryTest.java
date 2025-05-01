package dse.followme.services.beachcomb.tests;

import dse.followme.services.beachcomb.model.dtos.MotionDataDTO;
import dse.followme.services.beachcomb.model.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import dse.followme.services.beachcomb.mapper.MotionDataMapper;
import dse.followme.services.beachcomb.model.entities.MotionData;
import dse.followme.services.beachcomb.repository.MotionDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class MotionDataRepositoryTest {
    @Autowired
    private MotionDataRepository motionDataRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        motionDataRepository.deleteById(TestUtils.testMotionDataDTO1.getVin());
        motionDataRepository.deleteById(TestUtils.testMotionDataDTO2.getVin());
        motionDataRepository.deleteById(TestUtils.testMotionDataDTO3.getVin());
    }

    @Test
    public void givenVehicle_whenSettingData_thenChooseNearestVehicleAndMatch() {
        MotionData vehicle1 = MotionDataMapper.toEntity(TestUtils.testMotionDataDTO1);
        MotionData vehicle2 = MotionDataMapper.toEntity(TestUtils.testMotionDataDTO2);
        MotionData vehicle3 = MotionDataMapper.toEntity(TestUtils.testMotionDataDTO3);
        vehicle2.setRole(Role.LEADING_VEHICLE);
        motionDataRepository.saveAll(List.of(vehicle1, vehicle2, vehicle3));

        // the nearest vehicle of vehicle 3 should be vehicle 2, lets check:
        Optional<MotionData> nearestVehicle = motionDataRepository.findNearbyVehicleWithOppositeRole(
                vehicle3.getVin(),
                40_000,
                100_000
        );
        Assertions.assertFalse(nearestVehicle.isEmpty(), "Did not vehicles nearby");
        Assertions.assertTrue(vehicle2.getVin().equals(nearestVehicle.get().getVin()), "Vehicle 2 is not the nearest vehicle");
    }

    @Test
    public void givenTwoVehicles_whenTooFarAway_findNoMatch() {
        MotionDataDTO v1 = TestUtils.testMotionDataDTO1;
        MotionDataDTO v2 = TestUtils.testMotionDataDTO2;

        v2.setRole(Role.LEADING_VEHICLE);
        v1.setLatitude(0D);
        v2.setLatitude(0D);
        v1.setLongitude(0D);
        v2.setLongitude(fromMeterToLongitude(500D));

        MotionData vehicle1 = MotionDataMapper.toEntity(v1);
        MotionData vehicle2 = MotionDataMapper.toEntity(v2);

        motionDataRepository.saveAll(List.of(vehicle1, vehicle2));

        // the nearest vehicle of vehicle 3 should be vehicle 2, lets check:
        Optional<MotionData> nearestVehicle = motionDataRepository.findNearbyVehicleWithOppositeRole(
                vehicle2.getVin(),
                0.2,
                5_000
        );

        Assertions.assertTrue(nearestVehicle.isEmpty(), "Found nearby vehicle but shouldnt");
    }

    @Test
    public void givenTwoVehicles_whenUnder200Meter_findMatch() {
        MotionDataDTO v1 = TestUtils.testMotionDataDTO1;
        MotionDataDTO v2 = TestUtils.testMotionDataDTO2;

        v2.setRole(Role.LEADING_VEHICLE);
        v1.setLatitude(0D);
        v2.setLatitude(0D);
        v1.setLongitude(0D);
        v2.setLongitude(fromMeterToLongitude(150D));

        MotionData vehicle1 = MotionDataMapper.toEntity(v1);
        MotionData vehicle2 = MotionDataMapper.toEntity(v2);

        motionDataRepository.saveAll(List.of(vehicle1, vehicle2));

        // the nearest vehicle of vehicle 3 should be vehicle 2, lets check:
        Optional<MotionData> nearestVehicle = motionDataRepository.findNearbyVehicleWithOppositeRole(
                vehicle2.getVin(),
                0.2,
                5_000
        );

        Assertions.assertTrue(nearestVehicle.isPresent(), "No nearby vehicle found");
        Assertions.assertTrue(nearestVehicle.get().getVin().equals(vehicle1.getVin()), "Wrong vin found");
    }

    private double fromMeterToLongitude(Double distanceKm) {
        // Circumference of the Earth at the equator in kilometers
        double circumferenceEquatorKm = 40075.0;

        // Distance covered by one degree of longitude at the equator
        double kmPerDegreeLongitude = circumferenceEquatorKm / 360.0;

        // Change in longitude in degrees
        double changeInLongitude = distanceKm / kmPerDegreeLongitude;

        return changeInLongitude;
    }
}
