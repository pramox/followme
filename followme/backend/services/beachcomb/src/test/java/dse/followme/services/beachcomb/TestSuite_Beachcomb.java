package dse.followme.services.beachcomb;

import dse.followme.services.beachcomb.tests.BeachcombControllerTest;
import dse.followme.services.beachcomb.tests.MotionDataRepositoryTest;
import dse.followme.services.beachcomb.tests.MotionDataServiceTest;
import dse.followme.services.beachcomb.tests.PubsubTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BeachcombControllerTest.class,
        MotionDataRepositoryTest.class,
        MotionDataServiceTest.class,
        PubsubTest.class
})
public class TestSuite_Beachcomb {
}
