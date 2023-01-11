package com.increff.pos.pojo;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(indexes = {
    @Index(name="timeIndex", columnList = "time"),
})
@Entity
public class OrderPojo {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false)
    private int id;
    @Column(name="time", nullable=false)
    private LocalDateTime time;
}
