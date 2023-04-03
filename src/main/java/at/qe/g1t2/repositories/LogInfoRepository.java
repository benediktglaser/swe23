package at.qe.g1t2.repositories;

import at.qe.g1t2.model.LogInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogInfoRepository extends JpaRepository<LogInfo, Integer> {

    @Query(value ="SELECT apa.revtype as revtype, log_info.id,log_info.timestamp, log_info.changed_at,log_info.modified_by from log_info join userx_aud apa on log_info.id = apa.rev", nativeQuery = true)
    List<Object[]> joinAccessAud();
}
