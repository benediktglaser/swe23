package at.qe.g1t2.repositories;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SensorStationRepository extends JpaRepository<SensorStation, String>, JpaSpecificationExecutor<SensorStation> {

    SensorStation findSensorStationById(String uuid);

    List<SensorStation> getSensorStationsByAccessPoint(AccessPoint accessPoint);

    SensorStation findSensorStationByAccessPointAndDipId(AccessPoint accessPoint, Long dipId);

    List<SensorStation> getSensorStationsByGardener(Userx gardener);

    default Page<SensorStation> getSensorStationsByAccessPoint(Specification<SensorStation> spec, Pageable page, AccessPoint accessPoint){
        Specification<SensorStation> accessPointSpec = (root, query, cb) ->
                cb.equal(root.get("accessPoint"), accessPoint.getId());
       return findAll(Specification.where(spec).and(accessPointSpec),page);
    }
    default Page<SensorStation> getSensorStationsByAccessPointAndDipId(Specification<SensorStation> spec, Pageable page, AccessPoint accessPoint, Long DipId){
        Specification<SensorStation> accessPointSpec = (root, query, cb) ->
                cb.equal(root.get("accessPoint"), accessPoint.getId());
        Specification<SensorStation> dipIdSpec = (root, query, cb) ->
                cb.equal(root.get("dipId"), accessPoint.getId());
        return findAll(Specification.where(spec).and(accessPointSpec).and(dipIdSpec),page);
    }
}

