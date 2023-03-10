package at.qe.g1t2.model.demo;

import at.qe.g1t2.model.Userx;

/**
 * Just combines a user and its status.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
public class UserStatusInfo {

    private Userx user;
    private UserStatus status = UserStatus.OFFLINE;

    public UserStatusInfo(Userx user) {
            super();
            this.user = user;
    }

    public Userx getUser() {
            return user;
    }

    public void setUser(Userx user) {
            this.user = user;
    }

    public UserStatus getStatus() {
            return status;
    }

    public void setStatus(UserStatus status) {
            this.status = status;
    }

}
