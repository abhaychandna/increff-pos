package com.increff.pos.pojo;
import java.time.LocalDateTime;

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
    private int id;
    private LocalDateTime time;
}
