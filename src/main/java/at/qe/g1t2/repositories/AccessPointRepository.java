package at.qe.g1t2.repositories;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.Userx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

/**
 * Repository for managing {@link AccessPoint} entities.
 *
 */

public interface AccessPointRepository extends JpaRepository<AccessPoint, String>, JpaSpecificationExecutor<AccessPoint>, Serializable {

    AccessPoint findAccessPointById(String uuid);


}
