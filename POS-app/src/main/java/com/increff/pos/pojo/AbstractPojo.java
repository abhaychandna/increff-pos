package com.increff.pos.pojo;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractPojo {
    @Column(name = "created", nullable = false)
    private ZonedDateTime created;
    @Column(name = "updated", nullable = false)
    private ZonedDateTime updated;
    @Version
    @Column(name = "version")
    private long version;

    @PrePersist
    protected void onCreate() {
        updated = created = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = ZonedDateTime.now();
    }
}