package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.UsersFavourites;
import at.qe.g1t2.model.Userx;
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
    UserService userService;

    @Autowired
    SensorStationService sensorStationService;

    @Test
    @WithMockUser(username = "user2", authorities = {"USER"})
    void saveNewUsersFavouritesTest(){
        SensorStation sensorStation = sensorStationService.loadSensorStation("4e9bca4a-5df7-4d5f-af5f-4493458f57f7");
        UsersFavourites usersFavourites = new UsersFavourites();
        usersFavourites.setUser(userService.loadUser("user2"));
        usersFavourites.setSensorStation(sensorStation);
        usersFavouritesService.saveUsersFavourites(sensorStation, usersFavourites);
        Assertions.assertEquals(sensorStation.getUsersFavourites().size(), 1);
    }

    @Test
    @WithMockUser(username = "user2",authorities = {"USER"})
    void removeUserFavouritesTest(){
        SensorStation sensorStation = sensorStationService.loadSensorStation("a1a96ebd-e6b1-426e-87b1-9c9f72118e05");
        Userx user = userService.loadUser("user2");
        usersFavouritesService.removeFromUsersFavourites(sensorStation);
        Assertions.assertEquals(2,usersFavouritesService.getAllFavouritesSensorStationsForUser().size());
        user = userService.loadUser("user2");
        Assertions.assertEquals(2,user.getUsersFavourites().size());
        sensorStation = sensorStationService.loadSensorStation("a1a96ebd-e6b1-426e-87b1-9c9f72118e05");
        Assertions.assertEquals(0,sensorStation.getUsersFavourites().size() );

    }


}
