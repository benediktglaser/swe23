package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.UserRole;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for accessing and manipulating user data.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
@Component
@Scope("application")
public class UserService implements Serializable {

    @Autowired
    private UserxRepository userRepository;
    @Autowired
    private SensorStationRepository sensorStationRepository;


    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);


    /**
     * Returns a collection of all users.
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public Collection<Userx> getAllUsers() {
        LogMsg<String,Userx> msg = new LogMsg<>(LogMsg.LogType.CRUD_READ,Userx.class,null,"load All Users", getAuthenticatedUser().getId());
        LOGGER.info(msg.getMessage());

        return userRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public Page<Userx> getAllUsers(Specification<Userx> spec, Pageable page) {
        LogMsg<String,Userx> msg = new LogMsg<>(LogMsg.LogType.CRUD_READ,Userx.class,null,"load All Users", getAuthenticatedUser().getId());
        LOGGER.info(msg.getMessage());
        return userRepository.findAll(spec,page);
    }

    /**
     * Loads a single user identified by its username.
     *
     * @param username the username to search for
     * @return the user with the given username
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.username eq #username")
    @Transactional
    public Userx loadUser(String username) {
        return userRepository.findFirstByUsername(username);
    }

    /**
     * Saves the user. This method will also set {@link Userx#createDate} for new
     * entities or {@link Userx#updateDate} for updated entities. The user
     * requesting this operation will also be stored as {@link Userx#createDate}
     * or {@link Userx#updateUser} respectively.
     *
     * @param user the user to save
     * @return the updated user
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public Userx saveUser(Userx user) {
        LogMsg.LogType type;
        if (user.isNew()) {
            user.setCreateDate(LocalDateTime.now());
            user.setCreateUser(getAuthenticatedUser());
            type = LogMsg.LogType.CRUD_CREATE;
        } else {
            user.setUpdateDate(LocalDateTime.now());
            user.setUpdateUser(getAuthenticatedUser());
            type = LogMsg.LogType.CRUD_UPDATE;
        }
        Userx newUser = userRepository.save(user);
        LogMsg<String,Userx> msg = new LogMsg<>(type, Userx.class, newUser.getId(),null, getAuthenticatedUser().getId());

        LOGGER.info(msg.getMessage());
        return newUser;
    }

    /**
     * Deletes the user.
     *
     * @param user the user to delete
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(Userx user) {
        List<SensorStation> sensorStations = sensorStationRepository.getSensorStationsByGardener(user);
        sensorStations.forEach(x -> {

            x.setGardener(null);
            sensorStationRepository.save(x);
        });
        LogMsg<String,Userx> msg = new LogMsg<>(LogMsg.LogType.CRUD_DELETE, Userx.class, user.getId(),null, getAuthenticatedUser().getId());

        LOGGER.info(msg.getMessage());
        userRepository.delete(user);
    }

    private Userx getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LogMsg<String,Userx> msg = new LogMsg<>(LogMsg.LogType.WARN, Userx.class,null,"Call getAuthenticatedUser()", loadUser(auth.getName()).getId());
        LOGGER.warn(msg.getMessage());
        return userRepository.findFirstByUsername(auth.getName());
    }

    public void addSensorStationToUser(SensorStation sensorStation){
        Userx userx = getAuthenticatedUser();
        userx.getSensorStations().add(sensorStation);
       // sensorStation.getUserx().add(userx);
        userRepository.save(userx);
    }
    public void removeSensorStationToUser(SensorStation sensorStation){
        Userx userx = getAuthenticatedUser();
       //System.out.println(sensorStation.getUserx());
        userx.getSensorStations().remove(sensorStation);
       // sensorStation.getUserx().remove(userx);
        userRepository.save(userx);
    }


    public List<String> getAllGardenerUsernames(){
        return userRepository.findUserNameByRole(UserRole.GARDENER);
    }



}
