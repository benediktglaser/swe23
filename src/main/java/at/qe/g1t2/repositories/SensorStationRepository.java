package at.qe.g1t2.repositories;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Repository for managing {@link SensorStation} entities.
 *
 */

public interface SensorStationRepository extends JpaRepository<SensorStation, String>, JpaSpecificationExecutor<SensorStation>, Serializable {

    SensorStation findSensorStationById(String uuid);

    List<SensorStation> getSensorStationsByAccessPoint(AccessPoint accessPoint);

    SensorStation findSensorStationByAccessPointAndDipId(AccessPoint accessPoint, Long dipId);

    List<SensorStation> getSensorStationsByGardenerAndEnabledTrue(Userx gardener);

    List<SensorStation> getSensorStationByEnabledTrue();
    Set<SensorStation> getSensorStationsByUserxAndEnabledTrue(Userx user);

    SensorStation getSensorStationsByMac(String mac);


}

