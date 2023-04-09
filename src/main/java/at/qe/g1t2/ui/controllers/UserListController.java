package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.CollectionToPageConverter;
import at.qe.g1t2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Controller for the user list view.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
@Component
@Scope("view")
public class UserListController extends AbstractListController<String, Userx> implements Serializable {

    @Autowired
    private UserService userService;

    public UserListController() {
        this.setListToPageFunction(new CollectionToPageConverter<String, Userx>() {
            @Override
            public Page<Userx> retrieveData(Specification<Userx> spec, Pageable page) {
                return userService.getAllUsers(spec, page);
            }
        });
    }

    /**
     * Returns a list of all users.
     *
     * @return
     */


}
