package at.qe.g1t2.model;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class SensorDataTest {
    @Test
    public void testGetterSetter() {

        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        validator.validate(new PojoClassExcludedFields(PojoClassFactory.getPojoClass(SensorData.class),
                Set.of("createDate")));

        SensorData sensorData = new SensorData();
        LocalDateTime dateTest = LocalDateTime.now();
        sensorData.setCreateDate(dateTest);
        Assertions.assertEquals(dateTest, sensorData.getCreateDate());


    }


    @Test
    public void testSensorStationEqualsContract() {
        SensorStation sensorStation1 = new SensorStation();
        sensorStation1.setId(UUID.randomUUID());
        SensorStation sensorStation2 = new SensorStation();
        sensorStation2.setId(UUID.randomUUID());
        SensorData sensorData1 = new SensorData();
        sensorData1.setId(UUID.randomUUID());
        SensorData sensorData2 = new SensorData();
        sensorData2.setId(UUID.randomUUID());
        SensorData sensorData3 = new SensorData();
        sensorData3.setId(sensorData1.getId());


        EqualsVerifier.forClass(SensorData.class).
                withPrefabValues(SensorData.class, sensorData1, sensorData2).withPrefabValues(SensorStation.class,sensorStation1,sensorStation2)
                .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
        Assertions.assertEquals(sensorData1.hashCode(),sensorData3.hashCode());
    }

    @Test
    public void testCompareTo(){
        SensorData sensorData1 = new SensorData();
        SensorData sensorData2 = new SensorData();
        sensorData1.setMeasurement(0.45);
        sensorData2.setMeasurement(0.34);
        Assertions.assertEquals(-1, sensorData1.compareTo(sensorData2));
        Assertions.assertEquals(1, sensorData2.compareTo(sensorData1));

        sensorData2.setMeasurement(0.45);
        Assertions.assertEquals(0, sensorData2.compareTo(sensorData1));


    }
}
