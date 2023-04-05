package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.CollectionToPageConverter;
import jakarta.persistence.criteria.CriteriaBuilder;

import jakarta.persistence.criteria.Path;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Scope("view")
public class AccessPointController extends AbstractListController<String,AccessPoint> {

    @Autowired
    AccessPointService accessPointService;

    public AccessPointController() {
        this.setListToPageFunction(new CollectionToPageConverter<String, AccessPoint>() {
            @Override
            public Page<AccessPoint> retrieveData(Specification<AccessPoint> spec, Pageable page) {
                return accessPointService.getAllAccessPoints(spec,page);
            }
        });
    }
}
