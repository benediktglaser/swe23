package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.repositories.AccessPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@Scope("application")
public class AccessPointService {

    @Autowired
    AccessPointRepository accessPointRepository;



    public AccessPoint loadAccessPoint(UUID uuid){

        return accessPointRepository.findAccessPointById(uuid);
    }
}
