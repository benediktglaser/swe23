package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Controller for the admin view to manage users.
 */
@Component
@Scope("session")
public class AdminManagementController{

    @Autowired
    private UserService userService;

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
    }

    /**
     * Returns the currently displayed user.
     *
     * @return
     */
    public Userx getUser() {
        if (user == null) {
            user = new Userx();
        }
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
        user = null;
    }




}


