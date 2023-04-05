package at.qe.g1t2.services;

import at.qe.g1t2.model.LogInfo;
import at.qe.g1t2.repositories.LogInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("application")
public class LogInfoService {

    @Autowired
    private LogInfoRepository repository;


    public List<LogInfo> getAllLogEntry(){
        return convertObjToLog(repository.joinAccessAud());
    }


    private List<LogInfo>convertObjToLog(List<Object[]> objList){
        if(objList.isEmpty() || objList == null){
            return null;
        }
        List<LogInfo> list = new ArrayList<>();
        for (Object[] log:objList) {
            LogInfo logInfo =  new LogInfo();
            logInfo.setId((Integer)log[0]);
            Timestamp stamp = (Timestamp)log[2];
            logInfo.setChangeDate(stamp.toLocalDateTime());
            logInfo.setType(convertRevTypeToString((Byte)log[3]));
            logInfo.setUsername((String)log[4]);
            list.add(logInfo);


        }

        return list;
    }

    public boolean changeHasHappened(Long interval){
        return !repository.findLogInfoByChangeDateBetween(LocalDateTime.now().minusSeconds(interval),LocalDateTime.now()).isEmpty();
    }


    private String convertRevTypeToString(Byte revType){
        if(revType == 0){
            return "ADD";
        }
        if(revType == 1){
            return "MOD";
        }
        return "DEL";
    }
}
