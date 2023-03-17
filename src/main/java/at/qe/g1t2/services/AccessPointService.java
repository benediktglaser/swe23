package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.repositories.AccessPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.UUID;
/**
 * This Class saves/delete Accesspoints and allows tho remove/add Sensorstations
 */
@Component
@Scope("application")
public class AccessPointService {

    @Autowired
    AccessPointRepository accessPointRepository;


    @PreAuthorize("hasAuthority('ADMIN')")
    public AccessPoint loadAccessPoint(UUID uuid){

        return accessPointRepository.findAccessPointById(uuid);
    }
}
