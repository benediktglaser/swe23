package at.qe.g1t2.model;


import at.qe.g1t2.RestAPI.model.SensorStationDTO;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;


@SpringBootTest
class SensorStationDTOTest {

    @Test
    public void testGetterSetter() {

        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();


        validator.validate(new PojoClassExcludedFields(PojoClassFactory.getPojoClass(SensorStationDTO.class),
                Set.of("createDate", "timestamp")));

    }

}