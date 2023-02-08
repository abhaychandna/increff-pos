package com.increff.pos.pojo;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(
    uniqueConstraints = {
    @UniqueConstraint(name="unique_daysalespojo_date", columnNames = {"date"})
}
)
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

    public DaySalesPojo() {
    }
    public DaySalesPojo(ZonedDateTime date, Integer invoicedOrdersCount, Integer invoicedItemsCount, Double totalRevenue) {
        this.date = date;
        this.invoicedOrdersCount = invoicedOrdersCount;
        this.invoicedItemsCount = invoicedItemsCount;
        this.totalRevenue = totalRevenue;
    }
}
