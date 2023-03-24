package at.qe.g1t2.repositories;


import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.SensorStationGardener;
import at.qe.g1t2.model.UserRole;
import at.qe.g1t2.model.Userx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

import java.util.UUID;

public interface SensorStationGardenerRepository extends JpaRepository<SensorStationGardener, UUID> {

        SensorStationGardener findSensorStationGardenerById(UUID uuid);

        Collection<SensorStationGardener> findSensorStationsGardenersByGardener(Userx userx);

        Collection<SensorStationGardener> findSensorStationsGardenersBySensorStation(SensorStation sensorStation);
        @Query("SELECT DISTINCT u.sensorStation FROM SensorStationGardener u WHERE :gardener = u.gardener")
        Collection<SensorStation> getSensorStationsByGardener(@Param("gardener") Userx gardener);



}
