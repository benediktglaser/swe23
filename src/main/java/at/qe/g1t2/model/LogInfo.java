package at.qe.g1t2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import java.time.LocalDateTime;

@Entity
@RevisionEntity(LogListener.class)

public class LogInfo extends DefaultRevisionEntity {
    @Column(name = "MODIFIED_BY")
    private String username;

    @Column(name = "CHANGED_AT")
    private LocalDateTime changeDate = LocalDateTime.now();

    @Column(name ="REVTYPE")
    private Byte revtype;

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

    public void setType(Byte type) {
        this.revtype = type;
    }

    public Byte getType() {
        return revtype;
    }
}
