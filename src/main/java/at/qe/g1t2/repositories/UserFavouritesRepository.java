package at.qe.g1t2.repositories;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.UsersFavourites;
import at.qe.g1t2.model.Userx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface UserFavouritesRepository extends JpaRepository<UsersFavourites, UUID> {

    @Query("SELECT u.sensorStation FROM UsersFavourites u JOIN u.sensorStation s WHERE u.user = :username")
    Collection<SensorStation> getAllSensorStationsByUser(@Param("username")Userx username);


    //@Query("SELECT u FROM UsersFavourites u WHERE u.user = :username and u.sensorStation = :sensorStation")
    //UsersFavourites getUsersFavouritesBySensorStationAndUser(@Param("sensorStation")SensorStation sensorStation, @Param("username")Userx username);
    UsersFavourites getUsersFavouritesBySensorStationAndUser(SensorStation sensorStation, Userx user);

}
