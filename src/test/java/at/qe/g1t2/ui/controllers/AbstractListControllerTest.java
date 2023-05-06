package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import jakarta.persistence.criteria.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AbstractListControllerTest extends AbstractListController<String, AccessPoint> {
    @Autowired
    private AccessPointService accessPointServiceMock;

    @BeforeEach
    public void setUp() {
        this.setListToPageFunction((spec, page) -> accessPointServiceMock.getAllAccessPoints(spec, page));
    }

    @Test
    @DirtiesContext
    public void testCount() {
        int count = this.count(new HashMap<>());
        assertEquals(3, count);
    }

    @Test
    @DirtiesContext
    public void testLoadWithNoFilterAndNoSort() {
        List<AccessPoint> accessPoints = this.load(0, 3, new HashMap<>(), new HashMap<>());
        assertEquals(3, accessPoints.size());
        List<String> accessPointNames = accessPoints.stream().map(AccessPoint::getAccessPointName).collect(Collectors.toList());
        Assertions.assertTrue(accessPointNames.contains("office"));
    }

    @Test
    @DirtiesContext
    public void testLoadWithNoFilterButSortedName() {
        Map<String, SortMeta> sortBy = new HashMap<>();
        sortBy.put("accessPointName", SortMeta.builder().field("accessPointName").order(SortOrder.ASCENDING).build());
        List<AccessPoint> accessPoints = this.load(0, 3, sortBy, new HashMap<>());
        assertEquals(3, accessPoints.size());
        List<String> accessPointNames = accessPoints.stream().map(AccessPoint::getAccessPointName).collect(Collectors.toList());
        Assertions.assertTrue(accessPointNames.get(0).contains("first floor"));
        sortBy.put("accessPointName", SortMeta.builder().field("accessPointName").order(SortOrder.DESCENDING).build());
        accessPoints = this.load(0, 3, sortBy, new HashMap<>());
        assertEquals(3, accessPoints.size());
        accessPointNames = accessPoints.stream().map(AccessPoint::getAccessPointName).collect(Collectors.toList());
        Assertions.assertTrue(accessPointNames.get(0).contains("second floor"));
    }

    @Test
    @DirtiesContext
    public void testLoadWithFilterNameButNotSorted() {
        Map<String, FilterMeta> filters = new HashMap<>();
        FilterMeta filterMeta = FilterMeta.builder().field("accessPointName").filterValue("office").build();
        filters.put("accessPointName", filterMeta);


        List<AccessPoint> accessPoints = this.load(0, 3, new HashMap<>(), filters);
        assertEquals(1, accessPoints.size());
        List<String> accessPointNames = accessPoints.stream().map(AccessPoint::getAccessPointName).collect(Collectors.toList());
        Assertions.assertTrue(accessPointNames.get(0).contains("office"));
    }

    @Test
    @DirtiesContext
    public void testLoadWithFilterInAdvance() {
        String accessPointName = "office";
        this.getExtraSpecs().add(Specification.where((root, query, criteriaBuilder) -> {
            Path<String> accessPoint = root.get("accessPointName");
            return criteriaBuilder.equal(accessPoint, accessPointName);
        }));

        List<AccessPoint> accessPoints = this.load(0, 3, new HashMap<>(), new HashMap<>());
        assertEquals(1, accessPoints.size());
        List<String> accessPointNames = accessPoints.stream().map(AccessPoint::getAccessPointName).collect(Collectors.toList());
        Assertions.assertTrue(accessPointNames.get(0).contains("office"));
    }

    @Test
    @DirtiesContext
    public void testLoadWithFilterInAdvanceAndUserFilter() {
        Boolean enabled = true;
        this.getExtraSpecs().add(Specification.where((root, query, criteriaBuilder) -> {
            Path<String> accessPoint = root.get("enabled");
            return criteriaBuilder.equal(accessPoint, enabled);
        }));

        Map<String, FilterMeta> filters = new HashMap<>();
        FilterMeta filterMeta = FilterMeta.builder().field("accessPointName").filterValue("office").build();
        filters.put("accessPointName", filterMeta);

        List<AccessPoint> accessPoints = this.load(0, 3, new HashMap<>(), filters);
        assertEquals(1, accessPoints.size());
        List<String> accessPointNames = accessPoints.stream().map(AccessPoint::getAccessPointName).collect(Collectors.toList());
        Assertions.assertTrue(accessPointNames.get(0).contains("office"));
    }
}
