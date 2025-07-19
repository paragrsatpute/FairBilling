import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FairBillingTest {
    private FairBilling fairBilling;

    @Before
    public void setup() {
        fairBilling = new FairBilling();
    }

    @Test
    public void testLoadFile() {
        List<String> file = fairBilling.readDataToList("log.txt");
        assertFalse( file.isEmpty() );
        assertEquals("Data file is correct.", 11, file.size());
        System.out.println("Running test 1 - checking to load file...");
    }

    @Test
    public void testNullOrEmptyData() {
        assertFalse(fairBilling.verifyDataAsRequested(null));
        assertFalse(fairBilling.verifyDataAsRequested(""));
        System.out.println("Running test 2 - checking for null or empty data");
    }

    @Test
    public void testWrongData() {
        assertFalse(fairBilling.verifyDataAsRequested("xxx"));
        System.out.println("Running test 3 - checking for wrong data");
    }

    @Test
    public void testCorrectData() {
        assertTrue(fairBilling.verifyDataAsRequested("14:02:03 ALICE99 Start"));
        System.out.println("Running test 4 - checking for correct data");
    }

    @Test
    public void testNumberOfUser() {
        List<Map<String, Object>> userSessionResultList = fairBilling.processData(
                Arrays.asList("14:02:03 ALICE99 Start",
                        "14:02:05 CHARLIE End",
                        "14:02:34 ALICE99 End",
                        "14:02:58 ALICE99 Start",
                        "14:03:02 CHARLIE Start",
                        "14:03:33 ALICE99 Start",
                        "14:03:35 ALICE99 End",
                        "14:03:37 CHARLIE End",
                        "14:04:05 ALICE99 End",
                        "14:04:23 ALICE99 End",
                        "14:04:41 CHARLIE Start"));
        assertEquals(2, userSessionResultList.size());
        System.out.println("Running test 5 - checking for number of user");
    }

    @Test
    public void testUserSessionResult() {
        List<Map<String, Object>> userSessionResultList = fairBilling.processData(
                Arrays.asList("14:02:03 ALICE99 Start",
                        "14:02:05 CHARLIE End",
                        "14:02:34 ALICE99 End",
                        "14:02:58 ALICE99 Start",
                        "14:03:02 CHARLIE Start",
                        "14:03:33 ALICE99 Start",
                        "14:03:35 ALICE99 End",
                        "14:03:37 CHARLIE End",
                        "14:04:05 ALICE99 End",
                        "14:04:23 ALICE99 End",
                        "14:04:41 CHARLIE Start"));
        assertEquals("userName = ALICE99, numberOfSessions = 4, totalDurationInSec = 240", "userName = " + userSessionResultList.get(0).get("userName") + ", numberOfSessions = " + userSessionResultList.get(0).get("numberOfSessions") + ", totalDurationInSec = " + userSessionResultList.get(0).get("totalDurationInSec"));
        System.out.println("Running test 6 - checking for user session result");
    }


}
