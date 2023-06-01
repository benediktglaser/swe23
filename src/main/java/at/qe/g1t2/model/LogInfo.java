package at.qe.g1t2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This Entity is the structure of the Logging information which only admins are able to access.
 */

@Entity
@RevisionEntity(LogListener.class)
public class LogInfo extends DefaultRevisionEntity {
    @Column(name = "MODIFIED_BY")
    private String username;

    @Column(name = "CHANGED_AT")

    private LocalDateTime changeDate = LocalDateTime.now();

    @Column(name = "REVTYPE")
    private String revtype;


    @Column(name = "ID_OF_TARGET")
    private String idTarget;

    @Column(name ="TARGET_TYPE")
    private String targetType;

    public String getIdTarget() {
        return idTarget;
    }

    public void setIdTarget(String idTarget) {
        this.idTarget = idTarget;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public void setType(String type) {
        this.revtype = type;
    }

    public String getType() {
        return revtype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogInfo)) return false;
        if (!super.equals(o)) return false;
        LogInfo logInfo = (LogInfo) o;
        return Objects.equals(username, logInfo.username) && Objects.equals(changeDate, logInfo.changeDate) && Objects.equals(revtype, logInfo.revtype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, changeDate, revtype);
    }
}
