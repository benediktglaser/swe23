package at.qe.g1t2.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class SensorStationTest {
    @Test
    public void testGetterSetterSensorStation() {
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        validator.validate(new PojoClassExcludedFields(PojoClassFactory.getPojoClass(SensorStation.class),
                Set.of("createDate")));

        SensorStation sensorStation = new SensorStation();
        LocalDateTime dateTest = LocalDateTime.now();
        sensorStation.setCreateDate(dateTest);
        Assertions.assertEquals(dateTest, sensorStation.getCreateDate());


    }


    @Test
    public void testSensorStationEqualsContract() {

        SensorStation sensorStation1 = new SensorStation();
        sensorStation1.setId(UUID.randomUUID());
        SensorStation sensorStation2 = new SensorStation();
        sensorStation2.setId(UUID.randomUUID());
        SensorStation sensorStation3 = new SensorStation();
        sensorStation3.setId(sensorStation1.getId());

        EqualsVerifier.forClass(SensorStation.class).withPrefabValues(SensorStation.class, sensorStation1, sensorStation2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
        Assertions.assertEquals(sensorStation1.hashCode(),sensorStation3.hashCode());
    }

    @Test
    public void testCompareTo(){

        SensorStation sensorStation1 = new SensorStation();
        SensorStation sensorStation2 = new SensorStation();
        sensorStation1.setName("A");
        sensorStation2.setName("B");
        Assertions.assertEquals(-1, sensorStation2.compareTo(sensorStation1));
        Assertions.assertEquals(1, sensorStation1.compareTo(sensorStation2));

        sensorStation2.setName("A");
        Assertions.assertEquals(0, sensorStation2.compareTo(sensorStation1));


    }
}

