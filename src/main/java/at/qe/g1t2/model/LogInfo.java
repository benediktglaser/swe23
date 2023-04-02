package at.qe.g1t2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@RevisionEntity(LogListener.class)
public class LogInfo extends DefaultRevisionEntity {
    @Column(name = "MODIFIED_BY")
    private String username;

    @Column(name = "CHANGED_AT")
    private LocalDateTime changeDate = LocalDateTime.now();



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

}
