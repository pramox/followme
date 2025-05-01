package dse.followme.services.beachcomb.tests;

import dse.followme.services.beachcomb.model.dtos.MotionDataDTO;
import dse.followme.services.beachcomb.model.enums.Role;

import java.util.Date;

public class TestUtils {
    public static final String TEST_DATA_1_VIN = "test_vehicle_01";
    public static final Double TEST_DATA_1_LONG = 1D; // x coordinate
    public static final Double TEST_DATA_1_LAT = 1D; // y coordinate
    public static final Integer TEST_DATA_1_LANE= 1;
    public static final Double TEST_DATA_1_SPEED = 55D;

    public static final String TEST_DATA_2_VIN = "test_vehicle_02";
    public static final Double TEST_DATA_2_LONG = 2D; // x coordinate
    public static final Double TEST_DATA_2_LAT = 2D; // y coordinate
    public static final Integer TEST_DATA_2_LANE= 1;
    public static final Double TEST_DATA_2_SPEED = 55D;

    public static final String TEST_DATA_3_VIN = "test_vehicle_03";
    public static final Double TEST_DATA_3_LONG = 3D; // x coordinate
    public static final Double TEST_DATA_3_LAT = 3D; // y coordinate
    public static final Integer TEST_DATA_3_LANE= 1;
    public static final Double TEST_DATA_3_SPEED = 55D;


    public static Date date() {
        return new Date();
    }

    public static final MotionDataDTO testMotionDataDTO1 = new MotionDataDTO(
            TestUtils.TEST_DATA_1_VIN,
            TestUtils.TEST_DATA_1_LAT,
            TestUtils.TEST_DATA_1_LONG,
            Role.FOLLOWING_VEHICLE,
            TestUtils.TEST_DATA_1_LANE,
            TestUtils.TEST_DATA_1_SPEED,
            TestUtils.date()
    );

    public static final MotionDataDTO testMotionDataDTO2 = new MotionDataDTO(
            TestUtils.TEST_DATA_2_VIN,
            TestUtils.TEST_DATA_2_LAT,
            TestUtils.TEST_DATA_2_LONG,
            Role.FOLLOWING_VEHICLE,
            TestUtils.TEST_DATA_2_LANE,
            TestUtils.TEST_DATA_2_SPEED,
            TestUtils.date()
    );

    public static final MotionDataDTO testMotionDataDTO3 = new MotionDataDTO(
            TestUtils.TEST_DATA_3_VIN,
            TestUtils.TEST_DATA_3_LAT,
            TestUtils.TEST_DATA_3_LONG,
            Role.FOLLOWING_VEHICLE,
            TestUtils.TEST_DATA_3_LANE,
            TestUtils.TEST_DATA_3_SPEED,
            TestUtils.date()
    );

}
