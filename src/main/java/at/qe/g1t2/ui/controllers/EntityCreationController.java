package at.qe.g1t2.ui.controllers;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;

public interface EntityCreationController<K,T extends Persistable<K> & Serializable & Comparable<T>> {



    T loadEntity(K id);


    T getEntity();



}
