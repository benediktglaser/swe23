package at.qe.g1t2.services;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;

/**
 * This class creates the messages for the logger.
 */
public class LogMsg<K, T extends Persistable<K> & Serializable> {

    public enum LogType {
        CRUD_CREATE,
        CRUD_READ,
        CRUD_UPDATE,
        CRUD_DELETE,
        WARN,
        ERROR,
        CONNECTION_FAILURE,
        NEW_CONNECTION,
        FAILURE,
        CONNECTED,
        AVAILABLED,
        OTHER

    }

    private LogType type;
    private Class<T> target;

    private String id;
    private String extraMsg;

    private String actor;

    public LogMsg(LogType type, Class<T> target, String id, String extraMsg, String actor) {
        this.type = type;
        this.target = target;
        this.id = id;
        this.extraMsg = extraMsg;
        this.actor = actor;
    }

    public LogMsg(LogType type, Class<T> target, String actor) {
        this(type, target, null, null, actor);
    }


    public String getMessage() {
        String msg = "Operation: " + type + "\n" +
                "Entity: " + target + "\n";
        if (id != null) {
            msg = msg + "Id: " + id + "\n";
        }
        if (extraMsg != null) {
            msg = msg + "Detail: " + extraMsg + "\n";
        }
        return msg;
    }
}