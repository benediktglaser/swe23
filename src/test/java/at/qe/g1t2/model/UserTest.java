package at.qe.g1t2.model;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;

@SpringBootTest
public class UserTest {

    @Test
    public void testGetterSetterSensorStation() {
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        validator.validate(new PojoClassExcludedFields(PojoClassFactory.getPojoClass(Userx.class),
                Set.of("createDate","updateDate")));

        Userx userx = new Userx();
        LocalDateTime dateTest = LocalDateTime.now();
        userx.setCreateDate(dateTest);
        userx.setUpdateDate(dateTest);
        Assertions.assertEquals(dateTest, userx.getCreateDate());
        Assertions.assertEquals(dateTest, userx.getUpdateDate());


    }
}
