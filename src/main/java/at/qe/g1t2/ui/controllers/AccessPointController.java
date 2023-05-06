package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import org.primefaces.model.FilterMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@Scope("view")
public class AccessPointController extends AbstractListController<String,AccessPoint> {

    @Autowired
    AccessPointService accessPointService;


    public AccessPointController() {
        this.setListToPageFunction((spec, page) -> accessPointService.getAllAccessPoints(spec,page));
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return accessPointService.getAllAccessPoints().size();
    }
}
