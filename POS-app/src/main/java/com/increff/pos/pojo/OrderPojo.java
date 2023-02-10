package com.increff.pos.pojo;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(uniqueConstraints = {
    @UniqueConstraint(name="unique_orderpojo_time", columnNames = {"time"})
})
@Entity
public class OrderPojo extends AbstractPojo{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    @Column(name="time", nullable=false)
    private ZonedDateTime time;
}
