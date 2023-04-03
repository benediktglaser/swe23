package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.UsersFavourites;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserFavouritesRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Scope("application")
public class UsersFavouritesService {
    @Autowired
    private UserxRepository userRepository;

    @Autowired
    private SensorStationRepository sensorStationRepository;

    @Autowired
    private UserFavouritesRepository userFavouritesRepository;

    public UsersFavourites saveUsersFavourites(SensorStation sensorStation,UsersFavourites usersFavourites){
        if(usersFavourites.isNew()){
            LocalDateTime createDate = LocalDateTime.now();
            usersFavourites.setCreateDate(createDate);
            usersFavourites.setUser(getAuthenticatedUser());
            usersFavourites.setSensorStation(sensorStation);
            sensorStation.getUsersFavourites().add(usersFavourites);
            getAuthenticatedUser().getUsersFavourites().add(usersFavourites);
            return userFavouritesRepository.save(usersFavourites);
        }
        return userFavouritesRepository.save(usersFavourites);
    }



    private Userx getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }

}
