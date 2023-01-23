package com.increff.pos.model;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedData<T> {
    List<T> data;
    Integer recordsTotal;
    Integer recordsFiltered;
    Integer draw;
    
    public PaginatedData(List<T> data, Integer draw, Integer recordsTotal, Integer recordsFiltered) {
        this.data = data;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.draw = draw;
    }

}
