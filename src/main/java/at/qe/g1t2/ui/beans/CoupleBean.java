package at.qe.g1t2.ui.beans;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.VisibleSensorStationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This bean is responsible for managing the couple mode on the webserver.
 */
@Component
@Scope("session")
public class CoupleBean {

    @Autowired
    private VisibleSensorStationsService visibleSensorStationsService;

    private Set<AccessPoint> accessPointSet = new HashSet<>();


    public void resetCoupleMode(){
        for(AccessPoint accessPoint: accessPointSet){
            visibleSensorStationsService.resetVisibleList(accessPoint);
            accessPointSet.remove(accessPoint);
        }
    }

    public void addAccessPoint(AccessPoint accessPoint){
        accessPointSet.add(accessPoint);
    }
}
