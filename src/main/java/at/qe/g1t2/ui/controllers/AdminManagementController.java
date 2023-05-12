package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.configs.WebSecurityConfig;
import at.qe.g1t2.model.UserRole;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller for the admin view to manage users(Delete, edit,...).
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

    private boolean userRole;

    private boolean adminRole;

    private boolean gardenerRole;

    private boolean superAdminRole;


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
     * Returns the currently displayed user or creates an empty user
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
        Set<UserRole> set = new HashSet<>();
        if(userRole){
            set.add(UserRole.USER);
            user.setRoles(set);
        }
        if(adminRole){
            set.add(UserRole.ADMIN);
            user.setRoles(set);
        }
        if(gardenerRole){
            set.add(UserRole.GARDENER);
            user.setRoles(set);
        }
        if(superAdminRole){
            set.add(UserRole.USER);
            set.add(UserRole.ADMIN);
            set.add(UserRole.GARDENER);
            user.setRoles(set);
        }
        PasswordEncoder encoder = WebSecurityConfig.passwordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user = this.userService.saveUser(user);
    }

    /**
     * Action to delete the currently displayed user.
     */
    public void doDeleteUser() {
        this.userService.deleteUser(user);
        user = null;
    }

    public boolean isUserRole() {
        return userRole;
    }

    public void setUserRole(boolean userRole) {
        this.userRole = userRole;
    }

    public boolean isAdminRole() {
        return adminRole;
    }

    public void setAdminRole(boolean adminRole) {
        this.adminRole = adminRole;
    }

    public boolean isGardenerRole() {
        return gardenerRole;
    }

    public void setGardenerRole(boolean gardenerRole) {
        this.gardenerRole = gardenerRole;
    }

    public boolean isSuperAdminRole() {
        return superAdminRole;
    }

    public void setSuperAdminRole(boolean superAdminRole) {
        this.superAdminRole = superAdminRole;
    }
}


