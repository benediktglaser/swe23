package at.qe.g1t2.repositories;

import at.qe.g1t2.model.AccessPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccessPointRepository extends JpaRepository<AccessPoint, String> {

    AccessPoint findAccessPointById(String uuid);

}
