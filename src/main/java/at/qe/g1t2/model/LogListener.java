package at.qe.g1t2.model;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This class is listening to changes which are made in the webserver.
 */
public class LogListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        LogInfo logInfo = (LogInfo) revisionEntity;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            logInfo.setUsername(authentication.getName());
        } else {
            logInfo.setUsername("ACCESS_POINT_REGISTRATION");
        }
    }

}

