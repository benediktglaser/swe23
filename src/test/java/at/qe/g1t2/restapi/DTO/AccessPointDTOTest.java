package at.qe.g1t2.restapi.DTO;

import at.qe.g1t2.model.PojoClassExcludedFields;
import at.qe.g1t2.restapi.model.AccessPointDTO;
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
class AccessPointDTOTest {
    @Test
    public void testGetterSetter() {

        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();


        validator.validate(new PojoClassExcludedFields(PojoClassFactory.getPojoClass(AccessPointDTO.class),
                Set.of("createDate", "timestamp")));


    }

}