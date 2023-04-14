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

public interface SensorStationRepository extends JpaRepository<SensorStation, String>, JpaSpecificationExecutor<SensorStation>, Serializable {

    SensorStation findSensorStationById(String uuid);

    List<SensorStation> getSensorStationsByAccessPoint(AccessPoint accessPoint);

    SensorStation findSensorStationByAccessPointAndDipId(AccessPoint accessPoint, Long dipId);

    List<SensorStation> getSensorStationsByGardener(Userx gardener);

    Set<SensorStation> getSensorStationsByUserx(Userx user);
    @Query("SELECT u from SensorStation u")
    List<SensorStation> getAll();

    SensorStation getSensorStationsByMac(String mac);

    @Query("SELECT u from SensorStation u where u.accessPoint = :accessPoint and u.createDate >= u.accessPoint.lastCouplingDate")
    List<SensorStation> getAllNewSensorStationsByAccessPoint(@Param("accessPoint") AccessPoint accessPoint);
}

