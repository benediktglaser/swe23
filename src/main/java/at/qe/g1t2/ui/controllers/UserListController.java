package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the user list view.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
@Controller
@Scope("view")
public class UserListController extends AbstractListController<String, Userx> implements Serializable {

    @Autowired
    private UserService userService;

    public UserListController() {
        this.setListToPageFunction((spec, page) -> userService.getAllUsers(spec, page));
    }

    public List<String> getAllUsername(String query){

        return userService.getAllGardenerUsernames().stream().filter(x -> x.toLowerCase().startsWith(query.toLowerCase())).collect(Collectors.toList());
    }

    /**
     * Returns a list of all users.
     *
     * @return
     */


}
