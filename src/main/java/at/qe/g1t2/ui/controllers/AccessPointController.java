package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Collection;

@Controller
@Scope("view")
public class AccessPointController {
    @Autowired
    AccessPointService accessPointService;


    public Collection<AccessPoint> getAllAccessPoints(){
        return accessPointService.getAllAccessPoints();
    }



}
