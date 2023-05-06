package at.qe.g1t2.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

@FunctionalInterface
public interface CollectionToPageConverter<K,T extends Persistable<K>> extends Serializable {
    Page<T> retrieveData(Specification<T> spec, Pageable page);


}
