package at.qe.g1t2.repositories;

import at.qe.g1t2.model.AccessPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.List;

public interface AccessPointRepository extends JpaRepository<AccessPoint, String>, JpaSpecificationExecutor<AccessPoint> {

    AccessPoint findAccessPointById(String uuid);


}
