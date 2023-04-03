package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.UsersFavourites;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@WebAppConfiguration
public class UsersFavouritesServiceTest {
    @Autowired
    UsersFavouritesService usersFavouritesService;

    @Autowired
    SensorStationService sensorStationService;

    @Test
    @WithMockUser(username = "user2", authorities = {"USER"})
    void saveNewUsersFavouritesTest(){
        SensorStation sensorStation = sensorStationService.loadSensorStation("4e9bca4a-5df7-4d5f-af5f-4493458f57f7");
        UsersFavourites usersFavourites = new UsersFavourites();
        usersFavouritesService.saveUsersFavourites(sensorStation, usersFavourites);
        Assertions.assertEquals(sensorStation.getUsersFavourites().size(), 1);
    }


}
