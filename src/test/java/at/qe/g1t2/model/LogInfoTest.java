package at.qe.g1t2.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class LogInfoTest {

    @Test
    public void testSensorStationEqualsContract() {
        EqualsVerifier.forClass(LogInfo.class).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testLogInfoTestAttributes() {
        LocalDateTime time = LocalDateTime.now();
        LogInfo logInfo1 = new LogInfo();
        logInfo1.setType("ADD");
        logInfo1.setChangeDate(time);
        logInfo1.setUsername("user1");

        LogInfo logInfo2 = new LogInfo();
        logInfo2.setType("ADD");
        logInfo2.setChangeDate(time);
        logInfo2.setUsername("user1");
        Assertions.assertEquals(logInfo1.hashCode(), logInfo2.hashCode());
        Assertions.assertEquals(time, logInfo1.getChangeDate());
        Assertions.assertEquals("ADD", logInfo1.getType());
        Assertions.assertEquals("user1", logInfo1.getUsername());
    }


}
