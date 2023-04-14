package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.services.CollectionToPageConverter;
import jakarta.persistence.criteria.Path;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Controller
@Scope("view")
public abstract class AbstractListController<K,T extends Persistable<K> & Serializable & Comparable<T>> extends LazyDataModel<T> {


    private CollectionToPageConverter<K,T> collectionToPageConverterFunction;

    private List<Specification<T>> extraSpecs = new ArrayList<>();

    public void setListToPageFunction(CollectionToPageConverter<K, T> collectionToPageConverterFunction) {
        this.collectionToPageConverterFunction = collectionToPageConverterFunction;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return 0;
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Pageable page = createPage(first, pageSize, sortBy);
        Specification<T> spec = createSpecification(filterBy);
        Specification<T> finalSpec = combineSpecifications(spec);
        Page<T> entity = collectionToPageConverterFunction.retrieveData(finalSpec,page);
        setRowCount((int) entity.getTotalElements());
        return entity.getContent();
    }

    public List<Specification<T>> getExtraSpecs() {
        return extraSpecs;
    }


    private Pageable createPage(int first, int pageSize, Map<String, SortMeta> sortBy) {
        if (!sortBy.isEmpty()) {
            SortMeta sortMeta = sortBy.values().stream().toList().get(0);
            Sort.Direction direction = (sortMeta.getOrder().isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC);
            String field = sortMeta.getField();
            return PageRequest.of(first / pageSize, pageSize, Sort.by(direction, field));
        }
        return PageRequest.of(first / pageSize, pageSize);
    }

    private Specification<T> createSpecification(Map<String, FilterMeta> filterBy) {
        if (!filterBy.isEmpty()) {
            FilterMeta filterMeta = filterBy.values().stream().toList().get(0);
            String filterField = filterMeta.getField();
            String filterValue = filterMeta.getFilterValue().toString();
            return Specification.where((root, query, criteriaBuilder) -> {
                if (filterValue.isEmpty()) {
                    return null;
                }
                Path<String> field = root.get(filterField);
                return criteriaBuilder.like(criteriaBuilder.lower(field), "%" + filterValue.toLowerCase() + "%");
            });
        }
        return null;
    }

    private Specification<T> combineSpecifications(Specification<T> spec) {
        Specification<T> finalSpec = spec;
        for(Specification<T> specification: extraSpecs){
            if(spec == null || specification == null){
                finalSpec = (spec == null ? specification : spec);
            }
            else {
                finalSpec = finalSpec.and(specification);
            }
        }
        return finalSpec;
    }


}
