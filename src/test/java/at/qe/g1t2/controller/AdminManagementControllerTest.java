package at.qe.g1t2.controller;

import at.qe.g1t2.configs.WebSecurityConfig;
import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.PojoClassExcludedFields;
import at.qe.g1t2.model.UserRole;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.UserService;
import at.qe.g1t2.ui.controllers.AdminManagementController;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

@SpringBootTest
public class AdminManagementControllerTest {


    @Autowired
    private AdminManagementController adminManagementController;

    @Autowired
    private UserService userService;


    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testEditingUser() {
        Userx user = userService.loadUser("michael");
        user.setLastName("kuhn");
        adminManagementController.setUser(user);
        adminManagementController.doSaveUser();
        adminManagementController.doReloadUser();
        user = userService.loadUser("michael");
        Assertions.assertEquals("kuhn", user.getLastName());
    }


    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDeletingUser() {
        Userx user = userService.loadUser("michael");
        adminManagementController.setUser(user);
        adminManagementController.doDeleteUser();
        user = userService.loadUser("michael");
        Assertions.assertNull(user);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testSaveUserWithAllRoles() {
        Userx user = adminManagementController.getUser();
        user.setPassword("Hallo12");
        user.setUsername("test12");
        adminManagementController.setAdminRole(true);
        adminManagementController.setUserRole(true);
        adminManagementController.setGardenerRole(true);
        adminManagementController.setUser(user);
        adminManagementController.doSaveUser();
        adminManagementController.doReloadUser();
        adminManagementController.setUser(user);

        adminManagementController.doSaveUser();
        Userx userx = userService.loadUser("test12");

        Assertions.assertNotNull(userx);
        Assertions.assertTrue(userx.getRoles().contains(UserRole.ADMIN));
        Assertions.assertTrue(userx.getRoles().contains(UserRole.USER));
        Assertions.assertTrue(userx.getRoles().contains(UserRole.GARDENER));
    }

    @Test
    public void testGetterSetter() {
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        validator.validate(new PojoClassExcludedFields(PojoClassFactory.getPojoClass(AdminManagementController.class),
                Set.of("userService","user")));
    }

}

