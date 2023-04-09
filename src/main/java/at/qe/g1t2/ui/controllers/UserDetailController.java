package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.LogInfoRepository;
import at.qe.g1t2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Controller for the user detail view.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
@Component
@Scope("view")
public class UserDetailController implements Serializable {

    @Autowired
    private UserService userService;
    @Autowired
    private LogInfoRepository logInfoRepository;
    /**
     * Attribute to cache the currently displayed user
     */

    private Userx user;

    /**
     * Sets the currently displayed user and reloads it form db. This user is
     * targeted by any further calls of
     * {@link #doReloadUser()}, {@link #doSaveUser()} and
     * {@link #doDeleteUser()}.
     *
     * @param user
     */
    public void setUser(Userx user) {
        this.user = user;
        doReloadUser();
    }

    /**
     * Returns the currently displayed user.
     *
     * @return
     */
    public Userx getUser() {
        return user;
    }

    /**
     * Action to force a reload of the currently displayed user.
     */
    public void doReloadUser() {
        user = userService.loadUser(user.getUsername());
    }

    /**
     * Action to save the currently displayed user.
     */
    public void doSaveUser() {
        user = this.userService.saveUser(user);
    }

    /**
     * Action to delete the currently displayed user.
     */

    public void doDeleteUser() {
        this.userService.deleteUser(user);
      /*  System.out.println(logInfoRepository.findAll());
      logInfoRepository.joinAccessAud().forEach(x ->{

          System.out.println((Byte)x[0]);
          System.out.println((Integer) x[1]);
          System.out.println((String) x[4] );
              // Fügen Sie hier den Code ein, um mit den Daten zu arbeiten
      });*/
        user = null;
    }

}
