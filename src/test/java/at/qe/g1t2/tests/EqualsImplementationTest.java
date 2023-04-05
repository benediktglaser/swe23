package at.qe.g1t2.tests;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.model.UserRole;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * Tests to ensure that each entity's implementation of equals conforms to the
 * contract. See {@linkplain 'http://www.jqno.nl/equalsverifier/'} for more
 * information.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
@SpringBootTest
public class EqualsImplementationTest {

    @Test
    public void testUserEqualsContract() {
        Userx user1 = new Userx();
        user1.setUsername("user1");
        Userx user2 = new Userx();
        user2.setUsername("user2");
        SensorStation sensorStation1 = new SensorStation();
        sensorStation1.setId(UUID.randomUUID().toString());
        SensorStation sensorStation2 = new SensorStation();
        sensorStation2.setId(UUID.randomUUID().toString());
        EqualsVerifier.forClass(Userx.class).withPrefabValues(Userx.class, user1, user2).withPrefabValues(SensorStation.class, sensorStation1, sensorStation2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testUserRoleEqualsContract() {
        EqualsVerifier.forClass(UserRole.class).verify();
    }

}