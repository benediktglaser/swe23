package at.qe.g1t2.tests;


import at.qe.g1t2.ui.beans.SecurityTestBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
public class TestSecurityTestBean {

    @Autowired
    SecurityTestBean securityTestBean;


    @Test
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    void testAdmin() {
        securityTestBean.doAdminAction();
        Assertions.assertEquals("ADMIN", securityTestBean.getPerformedAction());
        Assertions.assertTrue(securityTestBean.isShowOkDialog());
    }

    @Test
    @WithMockUser(username = "elvis", authorities = {"GARDENER"})
    void testGardener() {
        securityTestBean.doGardenerAction();
        securityTestBean.doHideOkDialog();
        Assertions.assertEquals("GARDENER", securityTestBean.getPerformedAction());
        Assertions.assertFalse(securityTestBean.isShowOkDialog());
    }

    @Test
    @WithMockUser(username = "petersen", authorities = {"USER"})
    void testUser() {
        securityTestBean.doUserAction();
        Assertions.assertEquals("USER", securityTestBean.getPerformedAction());
        Assertions.assertTrue(securityTestBean.isShowOkDialog());
    }

}
