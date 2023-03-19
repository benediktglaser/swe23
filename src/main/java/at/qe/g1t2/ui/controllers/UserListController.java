package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * Controller for the user list view.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
@Component
@Scope("view")
public class UserListController implements Serializable {

    @Autowired
    private UserService userService;

    /**
     * Returns a list of all users.
     *
     * @return
     */
    public Collection<Userx> getUsers() {
        return userService.getAllUsers();
    }

}
