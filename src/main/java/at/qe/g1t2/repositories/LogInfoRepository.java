package at.qe.g1t2.repositories;

import at.qe.g1t2.model.LogInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogInfoRepository extends JpaRepository<LogInfo, Integer> {

    @Query(value = """
            SELECT log_info.id, log_info.timestamp, changed_at, ua.revtype, modified_by from log_info join userx_aud ua on log_info.id = ua.rev
            UNION ALL
            SELECT log_info.id, log_info.timestamp, changed_at, access_point_aud.revtype, modified_by from log_info join access_point_aud on log_info.id = access_point_aud.rev
            UNION ALL
            SELECT log_info.id, log_info.timestamp, changed_at, sda.revtype, modified_by from log_info join sensor_data_aud sda on log_info.id = sda.rev
            UNION ALL
            SELECT log_info.id, log_info.timestamp, changed_at, ssa.revtype, modified_by from log_info join sensor_station_aud ssa on log_info.id = ssa.rev
            UNION ALL
            SELECT log_info.id, log_info.timestamp, changed_at, sda.revtype, modified_by from log_info join sensor_data_type_info_aud sda on log_info.id = sda.rev;""", nativeQuery = true)
    List<Object[]> joinAccessAud();
}