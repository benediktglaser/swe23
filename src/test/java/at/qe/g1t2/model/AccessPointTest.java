package at.qe.g1t2.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests to ensure that each entity's implementation of equals conforms to the
 * contract. Additionally, compareTo and all getter/setters will be tested.
 */
@SpringBootTest
public class AccessPointTest {
    @Test
    public void testGetterSetterAccessPoint() {
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        validator.validate(new PojoClassExcludedFields(PojoClassFactory.getPojoClass(AccessPoint.class),
                Set.of("createDate", "updateDate")));
    }

    @Test
    public void testAccessPointEqualsContract() {
        AccessPoint accessPoint1 = new AccessPoint();
        accessPoint1.setAccessPointID(UUID.randomUUID().toString());
        AccessPoint accessPoint2 = new AccessPoint();
        accessPoint2.setAccessPointID(UUID.randomUUID().toString());
        SensorStation sensorStation1 = new SensorStation();
        sensorStation1.setId(UUID.randomUUID());
        SensorStation sensorStation2 = new SensorStation();
        sensorStation2.setId(UUID.randomUUID());
        EqualsVerifier.forClass(AccessPoint.class)
                .withPrefabValues(AccessPoint.class, accessPoint1, accessPoint2)
                .withPrefabValues(SensorStation.class, sensorStation1, sensorStation2)
                .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

}
