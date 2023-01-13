package com.increff.pos.pojo;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(indexes = {
    @Index(name="dateIndex", columnList = "date"),
})
@Entity
public class DaySalesPojo extends AbstractPojo{

    @Id
    @Column(name="date", nullable=false)
    private ZonedDateTime date;
    @Column(name="invoicedOrdersCount", nullable=false)
    private Integer invoicedOrdersCount;
    @Column(name="invoicedItemsCount", nullable=false)
    private Integer invoicedItemsCount;
    @Column(name="totalRevenue", nullable=false)
    private Double totalRevenue;
}
