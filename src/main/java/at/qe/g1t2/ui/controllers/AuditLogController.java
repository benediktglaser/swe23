package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.model.LogInfo;
import at.qe.g1t2.services.LogInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * This controller is responsible for retrieving log-entries from the audit-logger.
 */
@Controller
@Scope("view")
public class AuditLogController {
    @Autowired
    private LogInfoService logInfoService;

    List<LogInfo> infos;

    public List<LogInfo> getLogs() {
        if (infos == null) {
            infos = logInfoService.getAllLogEntry();
        }
        return infos;
    }
}
